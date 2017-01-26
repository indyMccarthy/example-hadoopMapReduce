Bases de données avancées
Big Data: Hadoop/MapReduce

# Prerrequisites

Verify that you have a working Java installation (with JAVA_HOME exported).

## Local instructions

Download hadoop:

http://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-2.7.1/hadoop-2.7.1.tar.gz
maven

You can try hadoop locally also using docker with:

    docker pull sequenceiq/hadoop-docker:2.7.1
    docker run -it sequenceiq/hadoop-docker:2.7.1 /etc/bootstrap.sh -bash

If you want to use the web UIs to check the progress of your program:

    docker ps
    docker inspect --format='{{.Name}} - {{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' d5f481740ecb

For example if the IP is 172.17.0.2 You find:

NameNode http://172.17.0.2:50070/
ResourceManager http://172.17.0.2:8088/
MapReduce JobHistory Server http://172.17.0.2:19888/

# Development Environment

- Verify you have a correct Java installation with the JAVA_HOME variable configured.
- Install an IDE or a decent editor
- Install maven in case you don't have it

# Setup a local working environment

Testing the installation
Assuming you installed Hadoop in HADOOP_PREFIX

You can run one of the stock examples:

    cd $HADOOP_PREFIX
    bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-*.jar grep input output 'dfs[a-z.]+'
    bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples*.jar wordcount LICENSE.txt output
    bin/hdfs dfs -cat output/*

Q: Execute it again, what happens ?

# Part 1 - Playing with Map Reduce

## HDFS

We are going to run the wordcount example of the course, so we need first to add the file to the distributed file system.

    bin/hadoop fs -mkdir hdfs://172.17.0.2:9000/tp1/

If it complains for permissions switch to the right user

    export HADOOP_USER_NAME=root
   
Upload the file

    bin/hadoop fs -put LICENSE.txt hdfs://172.17.0.2:9000/tp1/
    
Verify the upload

    bin/hadoop fs -ls hdfs://172.17.0.2:9000/tp1/
    
This one for remote IPs (port 8020 or 9000)

    bin/hadoop fs -ls hdfs://172.17.0.2/tp1/

## Running a MapReduce job in yarn

Copy the program to the cluster

    bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples*.jar wordcount hdfs://172.17.0.2:9000/tp1/LICENSE.txt hdfs://172.17.0.2:9000/tp1/output/$(date +%Y%m%d%H%M%S)

    bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples*.jar wordcount hdfs://172.17.0.2:9000/tp1/LICENSE.txt hdfs://172.17.0.2:9000/tp1/output
     
     
    bin/hadoop jar JAR_FILE CLASS input output

Verify the output

    hadoop fs -cat "hdfs://172.17.0.2:9000/tp1/output/part-*"

## Extra note

If you are interested in playing with more stuff of the hadoop ecosystem you can install one of the distributions, that brings everything packaged and ready:
via a virtual machine (Virtualbox) or docker, e.g.

Cloudera CDH quickstart

    docker pull cloudera/quickstart

If you are interested in the area, I strongly encourage you to install one of those and play 
with the multiple utils they offer. 


# Exercises:

1. Finish the implementation of Wordcount as seen in the class and validate that it works well.

You can use a file of your own or download a book from the gutenberg project for example.

2. Modify the implementation to return only the words that start with the letter 'm'.

Where did you change it, in the mapper or in the reducer ?
What are the consequences of changing this in the opposite ?
Compare the counters and argue about which one is the best strategy.

3. Use the GDELT dataset and Implement a Map Reduce job the top 10 news per country for a given time period (one week, one day, one month).

Add a combiner to the job ? Do you see any improvement in the counters ? Explain.

4. Calculate the average temperature for a file that has float measures.
Consider the possible issues. Can you use a combiner in this case ?


# Part 2 - Spark and Beam (WIP)

Prerrequisites:

http://d3kbcqa49mib13.cloudfront.net/spark-2.1.0-bin-hadoop2.7.tgz

    bin/spark-shell

# References

https://developer.yahoo.com/hadoop/tutorial/
http://blog.cloudera.com/blog/2009/08/hadoop-default-ports-quick-reference/

# Troubleshotting

- Error with winutils.exe

Add the environment variable HADOOP_HOME pointing to the directory with the missing windows files:

https://github.com/steveloughran/winutils/tree/master/hadoop-2.7.1/bin

export JAVA_HOME=/DIRECTORY/
export JAVA_LIBRARY_PATH=/DIRECTORY/
export LD_LIBRARY_PATH=/DIRECTORY/

# WIP

    spark-submit --class path.to.your.Class --master yarn --deploy-mode cluster [options] <app jar> [app options]
    
    
    
      % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                     Dload  Upload   Total   Spent    Left  Speed
    100  1318  100  1318    0     0   6979      0 --:--:-- --:--:-- --:--:--  7010
    [hadoop@ip-192-168-12-46 ~]$ ls
    hadoop-tps-1.0-SNAPSHOT.jar  kinglear.txt
    [hadoop@ip-192-168-12-46 ~]$ hadoop fs -mkdir /user/imejia/
    [hadoop@ip-192-168-12-46 ~]$ hadoop fs -mkdir /user/imejia/dataset
    [hadoop@ip-192-168-12-46 ~]$ hadoop fs -mkdir /user/imejia/output
    [hadoop@ip-192-168-12-46 ~]$ hadoop fs -put kinglear /user/imejia/dataset/
    put: `kinglear': No such file or directory
    ^C[hadoop@ip-192-168-12-46 ~]$ hadoop fs -put kinglear.txt /user/imejia/dataset/
    [hadoop@ip-192-168-12-46 ~]$ hadoop fs -ls /user/imejia/dataset/
hadoop fs -ls hdfs:///user/imejia/dataset/

hadoop fs -mkdir -p /user/testuser/output/wordcount

hadoop jar hadoop-tps-1.0.jar WordCount hdfs:///user/imejia/dataset/kinglear.txt hdfs:///user/imejia/output/wordcount/$(date +%Y%m%d%H%M%S)

hdfs://ip-192-168-12-46.ec2.internal:8020/user/imejia/output/kinglear
hadoop fs -rm -r hdfs:///user/imejia/output/*

hadoop fs -ls hdfs:///user/imejia/output/wordcount/
hadoop fs -cat hdfs:///user/imejia/output/kinglear/part-r-00000

ssh -i ~/IsmaelTest.pem -ND 8157 hadoop@ec2-54-175-250-7.compute-1.amazonaws.com

hadoop fs -mkdir -p /user/testuser/dataset
hadoop fs -mkdir -p /user/testuser/output/wordcount