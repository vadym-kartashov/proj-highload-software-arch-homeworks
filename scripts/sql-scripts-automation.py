import pymysql
import psycopg2
import threading
import time

# from pymysql import OperationalError, DatabaseError

host = "localhost"
user = "admin"
password = "admin"
database = "my_database"

# Iterate through transaction isolation levels and reproduce each concurrency problem
isolation_levels = [
    "READ UNCOMMITTED",
    "READ COMMITTED",
    "REPEATABLE READ",
    "SERIALIZABLE"
]

def lost_update(conn1, conn2, level):
    # Transaction T1 reads the row
    #conn1.begin()
    cursor1 = conn1.cursor()
    cursor1.execute("SELECT balance FROM accounts WHERE id = 1")
    value_t1 = cursor1.fetchone()[0]

    tread = threading.Thread(target=lost_update_inner, args=(conn1, conn2, level))
    tread.start()
    time.sleep(2)

    # Transaction T1 updates the row
    cursor1.execute("UPDATE accounts SET balance = %s WHERE id = 1", (value_t1 + 10,))
    conn1.commit()

    cursor1.execute("SELECT balance FROM accounts WHERE id = 1")
    conn1.rollback()
    # Check result
    print("lost_update " + ("Y" if cursor1.fetchone()[0] != 130 else "N"))


def lost_update_inner(conn1, conn2, level):
    #conn2.begin()
    cursor2 = conn2.cursor()
    # Transaction T2 reads the same row
    cursor2.execute("SELECT balance FROM accounts WHERE id = 1")
    value_t2 = cursor2.fetchone()[0]
    # Transaction T2 updates the row and commits
    cursor2.execute("UPDATE accounts SET balance = %s WHERE id = 1", (value_t2 + 20,))
    conn2.commit()


def dirty_read(conn1, conn2, level):
    #conn1.begin()
    cursor1 = conn1.cursor()
    # Transaction T1 updates the row
    cursor1.execute("UPDATE accounts SET balance = 200 WHERE id = 1")

    threading.Thread(target=dirty_read_inner, args=(conn1, conn2, level)).start()
    time.sleep(2)

    # Transaction T1 rolls back
    conn1.rollback()


def dirty_read_inner(conn1, conn2, level):
    #conn2.begin()
    cursor2 = conn2.cursor()
    # Transaction T2 reads the uncommitted change
    cursor2.execute("SELECT balance FROM accounts WHERE id = 1")
    print("dirty_read_inner " + ("Y" if cursor2.fetchone()[0] == 200 else "N"))


def non_repeatable_read(conn1, conn2, level):
    #conn1.begin()
    cursor1 = conn1.cursor()
    # Transaction T1 reads the row
    cursor1.execute("SELECT balance FROM accounts WHERE id = 1")
    initial_value = cursor1.fetchone()[0]
    threading.Thread(target=non_repeatable_read_inner, args=(conn1, conn2, level)).start()
    time.sleep(2)
    # Transaction T1 reads the row again
    cursor1.execute("SELECT balance FROM accounts WHERE id = 1")
    updated_value = cursor1.fetchone()[0]
    print("non_repeatable_read " + ("Y" if initial_value != updated_value else "N"))


def non_repeatable_read_inner(conn1, conn2, level):
    #conn2.begin()
    cursor2 = conn2.cursor()
    # Transaction T2 updates the row and commits
    cursor2.execute("UPDATE accounts SET balance = 250 WHERE id = 1")
    conn2.commit()


def phantom_read(conn1, conn2, level):
    #conn1.begin()
    cursor1 = conn1.cursor()
    # Transaction T1 reads rows based on a condition
    cursor1.execute("SELECT * FROM accounts WHERE balance < 150")
    initial_rows = cursor1.fetchall()

    threading.Thread(target=phantom_read_inner, args=(conn1, conn2, level)).start()
    time.sleep(2)

    # Transaction T1 reads again based on the same condition
    cursor1.execute("SELECT * FROM accounts WHERE balance < 150")
    updated_rows = cursor1.fetchall()

    print("phantom_read " + ("Y" if len(initial_rows) != len(updated_rows) else "N"))

def phantom_read_inner(conn1, conn2, level):
    #conn2.begin()
    cursor2 = conn2.cursor()
    # Transaction T2 inserts a new row that matches the condition and commits
    cursor2.execute("UPDATE accounts SET balance = 200 where id = 1")
    conn2.commit()


problems = [
    dirty_read,
    lost_update,
    non_repeatable_read,
    phantom_read
]

def reset_accounts_table():
    # conn = pymysql.connect(host=host, user=user, password=password, database=database)
    conn = psycopg2.connect(host=host, user=user, password=password, database=database)
    cursor = conn.cursor()
    cursor.execute("UPDATE accounts SET balance = 100")
    conn.commit()
    cursor.close()
    conn.close()


# Iterate through each isolation level and reproduce the problems
for level in isolation_levels:
    print(f"---- Testing for Isolation Level: {level} ----")
    conn1 = None
    conn2 = None
    for problem in problems:
        # try:x
        try:
            reset_accounts_table()
            # conn1 = pymysql.connect(host=host, user=user, password=password, database=database)
            # conn2 = pymysql.connect(host=host, user=user, password=password, database=database)
            conn1 = psycopg2.connect(host=host, user=user, password=password, database=database)
            conn2 = psycopg2.connect(host=host, user=user, password=password, database=database)
            cursor1 = conn1.cursor()
            cursor2 = conn2.cursor()
            cursor1.execute(f"SET SESSION TRANSACTION ISOLATION LEVEL {level}")
            cursor2.execute(f"SET SESSION TRANSACTION ISOLATION LEVEL {level}")

            problem(conn1, conn2, level)

        except pymysql.MySQLError as e:
            # Handle connection-related errors here
            print(f"Database error: {e}")
            # Optionally, rollback any changes made in the try block
            # conn1.rollback()
            # conn2.rollback()
        except Exception as e:
            # Handle other general exceptions here
            print(f"An error occurred: {e}")
            # conn1.rollback()
            # conn2.rollback()
        finally:
            # This block ensures that the connections are always closed
            conn1.close()
            conn2.close()
