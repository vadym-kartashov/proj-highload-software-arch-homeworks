import requests
import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()

# Retrieve GitHub token and username from environment variables
GITHUB_TOKEN = os.getenv('GITHUB_TOKEN')
GITHUB_USERNAME = os.getenv('GITHUB_USERNAME')

# Repositories to archive
repos = {
    "hsa3-resource-monitoring-systems": {"url": "https://github.com/vadym-kartashov/hsa3-resource-monitoring-systems",
                                         "branch": "master"},
    "hsa4-monitoring-user-metrics": {"url": "https://github.com/vadym-kartashov/hsa4-monitoring-user-metrics",
                                     "branch": "master"},
    "hsa9-sql-databases": {"url": "https://github.com/vadym-kartashov/hsa9-sql-databases", "branch": "main"},
    "hsa10-transactions-isolations-locks": {
        "url": "https://github.com/vadym-kartashov/hsa10-transactions-isolations-locks", "branch": "main"},
    "hsa11-nosql-elasticsearch": {"url": "https://github.com/vadym-kartashov/hsa11-nosql-elasticsearch",
                                  "branch": "main"},
    "hsa12-nosql-redis": {"url": "https://github.com/vadym-kartashov/hsa12-nosql-redis", "branch": "main"},
    "hsa13-queues": {"url": "https://github.com/vadym-kartashov/hsa13-queues", "branch": "main"},
    "hsa18-peak-loadings": {"url": "https://github.com/vadym-kartashov/hsa18-peak-loadings", "branch": "main"},
    "hsa20-data-structures-and-algorithms": {
        "url": "https://github.com/vadym-kartashov/hsa20-data-structures-and-algorithms", "branch": "main"},
    "hsa21-replication": {"url": "https://github.com/vadym-kartashov/hsa21-replication", "branch": "main"},
    "hsa22-sharding": {"url": "https://github.com/vadym-kartashov/hsa22-sharding", "branch": "main"},
    "hsa25-profiling": {"url": "https://github.com/vadym-kartashov/hsa25-profiling", "branch": "main"},
    "hsa26-ci-cd": {"url": "https://github.com/vadym-kartashov/hsa26-ci-cd", "branch": "main"},
    "hsa27-aws-ec2-elb": {"url": "https://github.com/vadym-kartashov/hsa27-aws-ec2-elb", "branch": "main"},
    "hsa28-aws-s3": {"url": "https://github.com/vadym-kartashov/hsa28-aws-s3", "branch": "main"},
    "hsa29-aws-autoscale": {"url": "https://github.com/vadym-kartashov/hsa29-aws-autoscale", "branch": "main"},
    "hsa30-serverless-calculations": {"url": "https://github.com/vadym-kartashov/hsa30-serverless-calculations",
                                      "branch": "master"}
}

headers = {
    'Authorization': f'token {GITHUB_TOKEN}',
    'Accept': 'application/vnd.github.v3+json'
}


# Function to archive a repository
def archive_repo(repo_name):
    url = f'https://api.github.com/repos/{GITHUB_USERNAME}/{repo_name}'
    data = {
        # 'archived': False,
        'private': True
    }
    response = requests.patch(url, json=data, headers=headers)
    return response


# Iterate over repositories and archive each one
for repo_name in repos.keys():
    response = archive_repo(repo_name)
    if response.status_code == 200:
        print(f'Successfully archived {repo_name}')
    else:
        print(f'Failed to archive {repo_name}. Status code: {response.status_code}, Response: {response.json()}')
