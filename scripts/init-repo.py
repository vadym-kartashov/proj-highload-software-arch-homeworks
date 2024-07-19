import glob
import os
import subprocess

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

exclude_files = list(repos.keys())
exclude_files.append(".git")
repos_to_process = []
if repos_to_process:
    repos = {k: v for k, v in repos.items() if k in repos_to_process}

target_repo_name = "proj-highload-software-arch-homeworks"

os.makedirs(target_repo_name, exist_ok=True)

os.chdir(target_repo_name)

subprocess.run(["git", "init"])

for folder, repo_info in repos.items():
    repo_url = repo_info["url"]
    repo_branch = repo_info["branch"]

    subprocess.run(["git", "remote", "add", folder, repo_url])

    subprocess.run(["git", "fetch", folder])

    subprocess.run(["git", "checkout", "-b", folder, f"{folder}/{repo_branch}"])

    os.makedirs(folder, exist_ok=True)

    files_to_move = [f for f in glob.glob("*") + glob.glob(".*") if f not in exclude_files]

    if files_to_move:
        subprocess.run(["git", "mv"] + files_to_move + [folder])

    subprocess.run(["git", "commit", "-m", f"Move {folder} content into {folder} folder"])

    subprocess.run(["git", "checkout", "master"])
    subprocess.run(["git", "merge", "--allow-unrelated-histories", "-m", f"Merge {folder} into master", folder])

subprocess.run(["git", "commit", "-m", "Merged all repositories into separate folders"])

target_repo_url = "https://github.com/vadym-kartashov/proj-highload-software-arch-homeworks.git"
subprocess.run(["git", "remote", "add", "origin", target_repo_url])
