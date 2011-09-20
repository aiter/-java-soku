#!/bin/bash

classpath=".:WEB-INF/classes"

for f in `ls WEB-INF/lib`
do
    classpath="$classpath:WEB-INF/lib/$f"
done

echo $classpath


java -cp $classpath com.youku.spider.core.client.Runner &
