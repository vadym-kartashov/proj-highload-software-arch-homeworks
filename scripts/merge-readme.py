import os

repos = [
    "hsa3-resource-monitoring-systems",
    "hsa4-monitoring-user-metrics",
    "hsa9-sql-databases",
    "hsa10-transactions-isolations-locks",
    "hsa11-nosql-elasticsearch",
    "hsa12-nosql-redis",
    "hsa13-queues",
    "hsa18-peak-loadings",
    "hsa20-data-structures-and-algorithms",
    "hsa21-replication",
    "hsa22-sharding",
    "hsa25-profiling",
    "hsa26-ci-cd",
    "hsa27-aws-ec2-elb",
    "hsa28-aws-s3",
    "hsa29-aws-autoscale",
    "hsa30-serverless-calculations"
]

main_readme = "README.full.md"

with open(main_readme, 'w') as main_file:
    main_file.write("# Combined Lecture Repositories\n\n")

for repo in repos:
    repo_readme = os.path.join('..', repo, "README.md")

    if os.path.isfile(repo_readme):
        with open(main_readme, 'a') as main_file:
            main_file.write(f"## [{repo}]({repo}/README.md)\n\n")
            with open(repo_readme, 'r') as readme_file:
                main_file.write(readme_file.read())
                main_file.write("\n\n")
    else:
        print(f"README.md not found in {repo}")

print(f"All README.md files have been merged into {main_readme}.")
