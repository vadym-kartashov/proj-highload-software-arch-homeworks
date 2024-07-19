import mysql.connector
import random
import time
from datetime import datetime

# Database configuration
config = {
    'user': 'mydb_user',
    'password': 'mydb_pwd',
    'host': 'localhost',
    'database': 'mydb'
}

# Connect to the database
conn = mysql.connector.connect(**config)
cursor = conn.cursor()

# Create the accounts table if it doesn't exist
cursor.execute('''
CREATE TABLE IF NOT EXISTS accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    dateOfBirth DATE,
    balance FLOAT
)
''')


def interpolate_query(query, params):
    # Convert all parameters to string and escape single quotes
    escaped_params = [str(param).replace("'", "''") for param in params]
    return query % tuple(escaped_params)


# Function to insert random values into the accounts table
def insert_random_values(duration):
    end_time = time.time() + duration
    first_names = ["John", "Jane", "Alice", "Bob", "Charlie"]
    last_names = ["Smith", "Doe", "Johnson", "Brown", "Williams"]

    while time.time() < end_time:
        first_name = random.choice(first_names)
        last_name = random.choice(last_names)
        dob = datetime.strptime(f"{random.randint(1900, 2000)}-{random.randint(1, 12)}-{random.randint(1, 28)}",
                                "%Y-%m-%d").date()
        balance = round(random.uniform(0, 10000), 2)

        sql_insert = """
        INSERT INTO accounts (firstName, lastName, dateOfBirth, balance)
        VALUES (%s, %s, %s, %s)
        """
        data_values = (first_name, last_name, dob, balance)

        # Print the interpolated query
        print(interpolate_query(sql_insert, data_values))

        # Execute the query
        cursor.execute(sql_insert, data_values)

        conn.commit()

        # Sleep for a short duration to prevent excessive inserts in a short time


# Call the function and let it run for a certain duration (e.g., 10 seconds)
insert_random_values(500)

# Print the last row

# Close the database connection
cursor.close()
conn.close()
