import time

import psycopg2.extras
import psycopg2
import random
from string import ascii_letters


def connect_to_db(port):
    # Connect to the PostgreSQL server using the provided port number
    return psycopg2.connect(
        dbname="postgres",
        user="postgres",
        password="postgres",
        host="localhost",
        port=port
    )


def execute_sql(prefix, port, sql):
    with connect_to_db(port) as conn, conn.cursor() as cur:
        start_time = time.time()  # Capture start time

        cur.execute(sql)

        end_time = time.time()  # Capture end time
        elapsed_time = end_time - start_time  # Calculate elapsed time
        print(f"{prefix} SQL {sql}  executed in {elapsed_time:.4f} seconds.")


def execute_many_sql(prefix, port, sql, records):
    with connect_to_db(port) as conn, conn.cursor() as cur:
        start_time = time.time()  # Capture start time
        psycopg2.extras.execute_values(cur, sql, records)
        end_time = time.time()  # Capture end time
        elapsed_time = end_time - start_time  # Calculate elapsed time
        print(f"{prefix} SQL {sql} {len(records)} executed in {elapsed_time:.4f} seconds.")


def generate_books_records(prefix, num, port):
    # Define helper functions
    def get_random_author():
        return ''.join(random.choices(ascii_letters, k=10))

    def get_random_title():
        return ''.join(random.choices(ascii_letters, k=15))

    # Prepare random records
    records = []
    for i in range(num):
        category_id = random.randint(1, 3)  # Assuming categories range from 1 to 5
        author = get_random_author()
        title = get_random_title()
        year = random.randint(1900, 2023)
        records.append((i, category_id, author, title, year))

    # Insert records using execute_values
    insert_query = "INSERT INTO books (id, category_id, author, title, year) VALUES %s"
    execute_many_sql(prefix, port, insert_query, records)


def generate_and_query_books(prefix, port):
    counts = [1000000, 100000, 10000, 100, 10]
    for count in counts:
        generate_books_records(prefix, count, port)

    queries = [
        "SELECT * FROM books;",
        "SELECT * FROM books WHERE category_id = 1;",
        "SELECT * FROM books WHERE category_id = 2;",
        "SELECT * FROM books WHERE category_id = 3;",
        "SELECT * FROM books WHERE category_id IN (1,2);",
        "SELECT * FROM books WHERE category_id IN (2,3);"
    ]

    for query in queries:
        execute_sql(prefix, port, query)


def execute_sql_script(filename, port):
    # Read the SQL file
    with open(filename, 'r') as file:
        sql_script = file.read()

    # Connect to the database and execute the script
    with connect_to_db(port) as conn:
        with conn.cursor() as cur:
            cur.execute(sql_script)

def main():
    # default
    generate_and_query_books("default", 4432)

    # fwd
    generate_and_query_books("fwd", 5432)

    execute_sql_script("./sql/citus.sql", 8432)
    # Citus
    generate_and_query_books("citus", 8432)


if __name__ == "__main__":
    main()
