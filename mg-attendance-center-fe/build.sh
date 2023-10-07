#!/bin/sh

echo "发布前请确保以下条件均已满足："
echo "1. 当前代码为最新代码，如未更新则执行 $ git pull"

echo -n "yes or no:"

read result

if [ $result != "yes" ]
then
  exit
fi

# nrm use hz (考虑到一些同学没装nrm，可以用以下命令代替)
npm config set registry http://192.168.110.26:8088/repository/npm-group/

user=$(whoami)

current=`date "+%Y%m%d %H:%M:%S"`

branch=`git symbolic-ref --short -q HEAD`

yarn

npm run build

git add .

git commit -m "feat(打包):项目打包 用户：$user | 分支：$branch | 时间：$current"

git push origin $branch

echo "==========================================="

echo "项目打包成功 ===> 用户：$user | 分支：$branch | 时间：$current"
