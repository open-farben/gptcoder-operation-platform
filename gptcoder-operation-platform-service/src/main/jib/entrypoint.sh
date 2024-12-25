# 创建新用户 "gptcoder"，如果不存在的话
#if id -u gptcoder >/dev/null 2>&1; then
#    echo "User 'gptcoder' already exists."
#else
#    addgroup -S gptcoder
#    adduser -S -G redis redis
#    echo "User 'gptcoder' created."
#fi
#
## 切换到新用户 "myuser"
#echo "Switching to user 'gptcoder'"
#
#su - gptcoder

exec java -Duser.timezone=GMT+08 -Dfile.encoding=UTF8 -Xms2048m -Xmx10240m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -cp @/app/jib-classpath-file @/app/jib-main-class-file