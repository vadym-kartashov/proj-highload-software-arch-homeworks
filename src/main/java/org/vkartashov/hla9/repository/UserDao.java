package org.vkartashov.hla9.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Date;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countUsersBornBeforeDate(Date date, UserIndexedDateOfBirthField field) {
        return countUsersWithDateCondition(date, field, DateCondition.BEFORE);
    }

    public int countUsersBornAfterDate(Date date, UserIndexedDateOfBirthField field){
        return countUsersWithDateCondition(date, field, DateCondition.AFTER);
    }

    public int countUsersBornBeforeOrAtDate(Date date, UserIndexedDateOfBirthField field) {
        return countUsersWithDateCondition(date, field, DateCondition.BEFORE_OR_AT);
    }

    public int countUsersBornAfterOrAtDate(Date date, UserIndexedDateOfBirthField field){
        return countUsersWithDateCondition(date, field, DateCondition.AFTER_OR_AT);
    }

    private int countUsersWithDateCondition(Date date, UserIndexedDateOfBirthField field, DateCondition condition) {
        return queryCount(String.format("SELECT COUNT(*) FROM users WHERE %s %s ?", field.getFieldName(), condition.getOperator()), date);
    }

    public void createUser(String firstName, String lastName, Date dateOfBirth) {
        jdbcTemplate.update(
                "INSERT INTO users (first_name, last_name, date_of_birth_no_index, date_of_birth_btree, date_of_birth_hash) VALUES (?, ?, ?, ?, ?)",
                firstName, lastName, dateOfBirth, dateOfBirth, dateOfBirth
        );
    }

    public void deleteUsersByFirstName(String firstName) {
        jdbcTemplate.update("DELETE FROM users WHERE first_name = ?", firstName);
    }

    private int queryCount(String sql, Date date) {
        return jdbcTemplate.query(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(date.getTime()));
            return ps;
        }, rs -> {
            rs.next();
            return rs.getInt(1);
        });
    }

    public void setFlushLogAtTransactionCommitControl(FlushLogAtTransactionCommitControl control) {
        jdbcTemplate.execute("SET GLOBAL innodb_flush_log_at_trx_commit = %d".formatted(control.getValue()));
    }

    @AllArgsConstructor
    @Getter
    public enum UserIndexedDateOfBirthField {
        NO_INDEX("date_of_birth_no_index", "No index"),
        BTREE("date_of_birth_btree", "B-tree index"),
        HASH("date_of_birth_hash", "Hash index");
        private final String fieldName;
        private final String description;
    }

    @AllArgsConstructor
    @Getter
    public enum DateCondition {
        BEFORE("<"),
        AFTER(">"),
        BEFORE_OR_AT("<="),
        AFTER_OR_AT(">=");

        private final String operator;
    }

    @AllArgsConstructor
    @Getter
    public enum FlushLogAtTransactionCommitControl {
        EACH_SECOND(0),
        EACH_COMMIT(1),
        LOG_EACH_COMMIT_AND_FLUSH_EACH_SECOND(2);
        private final int value;
    }

}