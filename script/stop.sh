#!/usr/bin/env bash
PROCESS=`ps -ef|grep jos.jar | grep -v sh |grep -v grep|grep -v PPID|awk '{ print $2}'`
for i in $PROCESS
do
  process_info=`ps aux|grep ${i}|grep -v grep | grep -v sh |grep -v PPID|awk '{ print $0}'`
  echo "Kill the $1 process ${process_info}"
  sudo kill $i
  echo $i
done