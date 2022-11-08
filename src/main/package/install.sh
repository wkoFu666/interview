#!/usr/bin/env bash

#参数值由pom文件传递
baseZipName="${package-name}-${activeProfile}" #压缩包名称  publish-test.zip的publish
packageName="${package-name}" #命令启动包名 xx.jar的xx
mainClass="${boot-main}" #java -cp启动时，指定main入口类;命令：java -cp conf;lib\*.jar;${packageName}.jar ${mainClass}


#固定变量
## 执行脚本所在的路径
basePath="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
baseDirPath="${basePath}" #解压部署磁盘路径
classPath=../lib
pid= #进程pid



#检测pid
function getPid()
{
    echo "检测状态---------------------------------------------"
    # shellcheck disable=SC2006
    # shellcheck disable=SC2009
    pid=`ps -ef | grep -n "${packageName}" | grep -v grep | awk '{print $2}'`
    if [ "${pid}" ]
    then
        echo "运行pid：${pid}"
    else
        echo "未运行"
    fi
}


#启动程序
function start()
{

    #启动前，先停止之前的
    stop
    if [ "${pid}" ]
    then
        echo "停止程序失败，无法启动"
    else
        echo "启动程序---------------------------------------------"

       # shellcheck disable=SC2045
        for each_lib in $(ls ../lib); do
          classPath=$classPath:../lib/$each_lib
        done
       # shellcheck disable=SC2086
       nohup java -classpath $classPath "${mainClass}" >${baseDirPath}/${packageName}.out 2>&1 &

        #查询是否有启动进程
        getPid
        if [ "${pid}" ]
        then
            echo "已启动"
            #nohup日志
            tail -n 50 -f "${baseDirPath}"/"${packageName}".out
        else
            echo "启动失败"
        fi
    fi
}

#停止程序
function stop()
{
    getPid
    if [ "${pid}" ]
    then
        echo "停止程序---------------------------------------------"
        kill -9 "${pid}"

        getPid
        if [ "${pid}" ]
        then
            #stop
            echo "停止失败"
        else
            echo "停止成功"
        fi
    fi
}




#-------------------------启动----------------
#启动时带参数，根据参数执行
if [ ${#} -ge 1 ]
then
    case ${1} in
        "start")
            start
        ;;
        "restart")
            start
        ;;
        "stop")
            stop
        ;;
        *)
            echo "${1}无任何操作"
        ;;
    esac
else
    echo "
    command如下命令：
    start：启动
    stop：停止进程
    restart：重启

    示例命令如：./install.sh start
    "
fi