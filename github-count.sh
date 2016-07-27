#!/bin/bash

# Clone a repo passed as command line and count commits/merges ... made
# on the GitHub UI, by special committer email noreply@github.com

byebye() {
  echo "Usage: $0 remote-git-repo"
  exit 1
}

if [ "$#" -ne 1 ]
then
    byebye
fi

repo_url="$1"

# Cut out the repo_name from the clone URL .../<repo-name>.git...
repo_name=`echo "$repo_url" | sed "s/.*\/\(.*\)\.git.*/\1/g"`

# Clone the repo quietly 
echo "Cloning '$repo_url' into '$repo_name'..."
git clone --quiet $repo_url $repo_name

# Check if cloning worked
if [ "$?" -ne 0 ]
then
  echo "Could not git-clone '$repo_url'"
  byebye
fi

# Change into repo
cd $repo_name

# Grep count for UI committer
echo "Counting commits/merges/... made on GitHub web UI"
count="`git --no-pager log --pretty=format:'%ce' | grep -c noreply@github.com`"
echo "Result: $count"

# Go back
cd ..

