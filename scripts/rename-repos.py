import requests
from dotenv import load_dotenv
import os

load_dotenv()

GITHUB_TOKEN = os.getenv('GITHUB_TOKEN')
GITHUB_USERNAME = os.getenv('GITHUB_USERNAME')
if not GITHUB_TOKEN or not GITHUB_USERNAME:
    raise ValueError('Please provide GITHUB_TOKEN and GITHUB_USERNAME in .env file')

repo_rename_map = {
    "hla3": "hsa3-resource-monitoring-systems",
    "hla4": "hsa4-monitoring-user-metrics",
    "hla9": "hsa9-sql-databases",
    "hla10-locks": "hsa10-transactions-isolations-locks",
    "hla11": "hsa11-nosql-elasticsearch",
    "hla12": "hsa12-nosql-redis",
    "hla13-queues": "hsa13-queues",
    "hla18-peak-loadings": "hsa18-peak-loadings",
    "hla20-data-structures-and-algorithms": "hsa20-data-structures-and-algorithms",
    "hla21-replication": "hsa21-replication",
    "hla22-sharding": "hsa22-sharding",
    "hla25-profiling": "hsa25-profiling",
    "hla26-ci-cd": "hsa26-ci-cd",
    "hla27-aws-ec2-elb": "hsa27-aws-ec2-elb",
    "hla28-aws-s3": "hsa28-aws-s3",
    "hla29-aws-autoscale": "hsa29-aws-autoscale",
    "hla30-serverless-calculations": "hsa30-serverless-calculations"
}

headers = {
    'Authorization': f'token {GITHUB_TOKEN}',
    'Accept': 'application/vnd.github.v3+json'
}

for old_name, new_name in repo_rename_map.items():
    url = f'https://api.github.com/repos/{GITHUB_USERNAME}/{old_name}'
    data = {
        'name': new_name
    }

    response = requests.patch(url, json=data, headers=headers)

    if response.status_code == 200:
        print(f'Successfully renamed {old_name} to {new_name}')
    else:
        print(f'Failed to rename {old_name}. Status code: {response.status_code}, Response: {response.json()}')
