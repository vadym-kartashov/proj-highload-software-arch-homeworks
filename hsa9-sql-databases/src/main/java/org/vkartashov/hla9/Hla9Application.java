package org.vkartashov.hla9;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.vkartashov.hla9.repository.UserDao;
import org.vkartashov.hla9.util.DateUtil;
import org.vkartashov.hla9.util.StopWatchUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.vkartashov.hla9.util.Constants.*;

@SpringBootApplication
public class Hla9Application implements CommandLineRunner {

	public static final Logger LOG = LoggerFactory.getLogger(Hla9Application.class);

	@Value("${hla9.concurrentInserts.threads:10}")
	private int threadsNum;
	@Value("${hla9.concurrentInserts.recordsNum:1000000}")
	private int recordsNum;

	@Autowired
	private UserDao userDao;

	public static void main(String[] args) {
		new SpringApplicationBuilder(Hla9Application.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}

	@Override
	public void run(String... args) {
		List<MeasurementRoutineItem> routine = new ArrayList<>();
		routine.addAll(buildRoutineItemsForField(UserDao.UserIndexedDateOfBirthField.NO_INDEX));
		routine.addAll(buildRoutineItemsForField(UserDao.UserIndexedDateOfBirthField.BTREE));
		routine.addAll(buildRoutineItemsForField(UserDao.UserIndexedDateOfBirthField.HASH));
		routine.addAll(prepareDbInsertRoutine());
		for (MeasurementRoutineItem item : routine) {
			long executionTime = StopWatchUtil.measureExecutionTime(item);
			LOG.info(item.getKey() + " (ms) : " + executionTime + " ms");
		}
	}

	private List<MeasurementRoutineItem> prepareDbInsertRoutine() {
		List<MeasurementRoutineItem> routine = new ArrayList<>();
		routine.add(prepareDbInsertRoutineItem(UserDao.FlushLogAtTransactionCommitControl.EACH_SECOND));
		routine.add(prepareDbInsertRoutineItem(UserDao.FlushLogAtTransactionCommitControl.EACH_COMMIT));
		routine.add(prepareDbInsertRoutineItem(UserDao.FlushLogAtTransactionCommitControl.LOG_EACH_COMMIT_AND_FLUSH_EACH_SECOND));
		return routine;
	}

	private MeasurementRoutineItem prepareDbInsertRoutineItem(UserDao.FlushLogAtTransactionCommitControl control) {
		return new MeasurementRoutineItem(control.name(), key -> {
			userDao.deleteUsersByFirstName(INSERTS_ROUTINE_DUMMY_USER_FIRST_NAME);
			userDao.setFlushLogAtTransactionCommitControl(control);
			ExecutorService executorService = Executors.newFixedThreadPool(threadsNum);
			AtomicInteger successCounter = new AtomicInteger();
			for (int i = 0; i < recordsNum; i++) {
				Date dateOfBirth = DateUtil.generateRandomDateBetween(DateUtil.parseDate(GENERATED_DATES_START_RANGE), DateUtil.parseDate(GENERATED_DATES_END_RANGE));
				executorService.submit(() -> {
					try {
						userDao.createUser(INSERTS_ROUTINE_DUMMY_USER_FIRST_NAME, INSERTS_ROUTINE_DUMMY_USER_FIRST_NAME, dateOfBirth);
						successCounter.incrementAndGet();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
			try {
				executorService.shutdown();
				executorService.awaitTermination(1, TimeUnit.HOURS);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			userDao.deleteUsersByFirstName(INSERTS_ROUTINE_DUMMY_USER_FIRST_NAME);
			userDao.setFlushLogAtTransactionCommitControl(UserDao.FlushLogAtTransactionCommitControl.EACH_COMMIT);
		});
	}

	private List<MeasurementRoutineItem> buildRoutineItemsForField(UserDao.UserIndexedDateOfBirthField field) {
		List<MeasurementRoutineItem> routine = new ArrayList<>();
		routine.add(new MeasurementRoutineItem("1%" + field.getDescription(), key -> {
			int count = userDao.countUsersBornBeforeOrAtDate(DateUtil.parseDate(ONE_PERCENT_DATE), field);
			LOG.info(key + " count " + count);
		}));
		routine.add(new MeasurementRoutineItem("50% #1" + field.getDescription(), key -> {
			int count = userDao.countUsersBornBeforeOrAtDate(DateUtil.parseDate(FIFTY_PERCENT_DATE), field);
			LOG.info(key + " count " + count);
		}));
		routine.add(new MeasurementRoutineItem("50% #2" + field.getDescription(), key -> {
			int count = userDao.countUsersBornAfterDate(DateUtil.parseDate(FIFTY_PERCENT_DATE), field);
			LOG.info(key + " count " + count);
		}));
		routine.add(new MeasurementRoutineItem("99%" + field.getDescription(), key -> {
			int count = userDao.countUsersBornAfterDate(DateUtil.parseDate(ONE_PERCENT_DATE), field);
			LOG.info(key + " count " + count);
		}));
		return routine;
	}

}
