import json
import requests

# Replace the following with your Elasticsearch server's configuration
ELASTICSEARCH_HOST = 'localhost'
ELASTICSEARCH_PORT = 9200

# Index name and mapping definition
INDEX_NAME = 'autocomplete'

MAPPING = {
        "mappings": {
            "properties" : {
                "title" : {
                    "type": "text"
                },
               "length": {
                   "type": "integer"
               },
                "root": {
                    "type": "text"
                },
                "suffix": {
                    "type": "text"
                }
            }
        }
}

# Function to create the index and mappings using HTTP requests
def create_index():
    url = f"http://{ELASTICSEARCH_HOST}:{ELASTICSEARCH_PORT}/{INDEX_NAME}"
    headers = {'Content-Type': 'application/json'}

    response = requests.put(url, data=json.dumps(MAPPING), headers=headers)
    if response.status_code == 200:
        print(f"Index '{INDEX_NAME}' created with mappings.")
    else:
        print(f"Error creating index '{INDEX_NAME}': {response.text}")
        exit(-1)

# Function to fill the index with data from words.txt using HTTP requests
def fill_index(input_file):
    url = f"http://{ELASTICSEARCH_HOST}:{ELASTICSEARCH_PORT}/{INDEX_NAME}/_bulk"
    headers = {'Content-Type': 'application/x-ndjson'}

    bulk_data = ''
    with open(input_file, 'r') as words_file:
        for word in words_file:
            word = word.strip()
            if word:
                data = {
                    "title": word,
                    "length": len(word),
                    "root": word[:7],
                    "suffix": word[7:]
                }
                bulk_data += f'{{"index": {{"_index": "{INDEX_NAME}"}}}}\n'
                bulk_data += json.dumps(data) + '\n'

    if bulk_data:
        response = requests.post(url, data=bulk_data, headers=headers)
        if response.status_code == 200:
            print(f"Data added to the '{INDEX_NAME}' index.")
        else:
            print(f"Error adding data to index '{INDEX_NAME}': {response.text}")
            exit(-1)

if __name__ == "__main__":
    input_file = "words.txt"

    create_index()
    fill_index(input_file)