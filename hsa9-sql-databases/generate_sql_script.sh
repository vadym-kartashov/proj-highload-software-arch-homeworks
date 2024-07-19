#!/bin/bash


# Output file
SQL_SCRIPT="create_insert_users.sql"
# Number of records to generate


# Truncate the file to start fresh
echo "DROP DATABASE IF EXISTS hla9;" > $SQL_SCRIPT
echo "CREATE DATABASE hla9;" >> $SQL_SCRIPT
echo "USE hla9;" >> $SQL_SCRIPT

# Table to insert into
TABLE_NAME="users"

# Generate SQL for table creation
SQL_CREATE_TABLE="
CREATE TABLE ${TABLE_NAME} (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    date_of_birth_no_index DATE,
    date_of_birth_btree DATE,
    date_of_birth_hash DATE
);
"

# Write SQL table creation to the file
echo $SQL_CREATE_TABLE >> $SQL_SCRIPT

# List of possible first and last names
FIRST_NAMES=("John" "Jane" "Tom" "Lucy" "Mike" "Emily")
LAST_NAMES=("Smith" "Johnson" "Brown" "Taylor" "Miller" "Clark")

# Start the SQL Insert command
SQL="INSERT INTO ${TABLE_NAME} (first_name, last_name, date_of_birth_no_index, date_of_birth_btree, date_of_birth_hash) VALUES "

for i in {1..40000000}
do
    # Generate random first name, last name and date of birth
    FIRST_NAME="${FIRST_NAMES[$RANDOM % ${#FIRST_NAMES[@]}]}"
    LAST_NAME="${LAST_NAMES[$RANDOM % ${#LAST_NAMES[@]}]}"
    DATE_OF_BIRTH="$(($RANDOM % 51 + 1950))-$((RANDOM % 12 + 1))-$((RANDOM % 28 + 1))"

    # Append to SQL variable
    SQL+="('${FIRST_NAME}', '${LAST_NAME}', '${DATE_OF_BIRTH}', '${DATE_OF_BIRTH}' ,'${DATE_OF_BIRTH}'),"

    # To prevent the line getting too long, we split the query every 1000 records
    if (( $i % 1000 == 0 )); then
        SQL=${SQL%?};
        SQL+=";"
        echo $SQL >> $SQL_SCRIPT
        SQL="INSERT INTO ${TABLE_NAME} (first_name, last_name, date_of_birth_no_index, date_of_birth_btree, date_of_birth_hash) VALUES "
    fi
done

# If there's any remaining query, write it to the file
if [[ "$SQL" != "INSERT INTO ${TABLE_NAME} (first_name, last_name, date_of_birth_no_index, date_of_birth_btree, date_of_birth_hash) VALUES " ]]; then
    SQL=${SQL%?};
    SQL+=";"
    echo $SQL >> $SQL_SCRIPT
fi

# Generate SQL for creating index
SQL_CREATE_INDEX_BTREE="CREATE INDEX idx_${TABLE_NAME}_date_of_birth_btree ON ${TABLE_NAME} (date_of_birth_btree) USING BTREE;"
SQL_CREATE_INDEX_HASH="CREATE INDEX idx_${TABLE_NAME}_date_of_birth_hash ON ${TABLE_NAME} (date_of_birth_hash) USING HASH;"
# Write SQL index creation to the file
echo $SQL_CREATE_INDEX_BTREE >> $SQL_SCRIPT
echo $SQL_CREATE_INDEX_HASH >> $SQL_SCRIPT

echo "SQL script has been generated: ${SQL_SCRIPT}"
