HDFS over WEBDAV
================

This code is the modified version of http://www.hadoop.iponweb.net to make it compatible with new version of Hadoop.
This version of hdfs-webdav works with Hadoop 0.20.1, you can checkout the previous commits on my git repository for older version of Hadoop.

INSTALLATION
============

1. Extract the source code and Use ant to compile :

$ ant -Dhadoop.dir=/opt/hadoop-0.20.1

2. Copy all the file under bin/ and conf/ to corresponding directory in hadoop dir:

$ cp bin/* /opt/hadoop-0.20.1/bin/
$ cp conf/* /opt/hadoop-0.20.1/conf/

3. Modify config file hadoop-webdav.sh to satisfy your configuration:

    * HADOOP_WEBDAV_HOST, HADOOP_WEBDAV_PORT - address and port WebDAV server will listen to.
    * HADOOP_WEBDAV_HDFS - The name of the HDFS, e.g. namenode:port in case if you run WebDAV server on nodes that are different from the master. If this parameter is not specified, WebDAV will try determine name of the FS from 'fs.default.name' parameter, specified in hadoop-site.xml of your Hadoop installation.
    * HADOOP_WEBDAV_CLASSPATH parameter should point to lib directory from where you unpacked WebDAV distribution.

4. Start your webdav server:
$ /opt/hadoop-0.20.1/bin/start-webdav.sh

Notice
======
Read the file README.old to get more information.



Huy Phan <dachuy@gmail.com>
