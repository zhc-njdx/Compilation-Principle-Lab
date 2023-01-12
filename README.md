# 编译原理Lab7

## 实验过程

> [本次实验](http://47.122.3.40:8081/#/lab7-while/lab7-while)实现了对`while`结构以及`break`和`continue`语句的翻译

鉴于前几次实验的基础之上，本次实验代码量较少比较简单，只需分别重写三个语句的`visitor`函数即可，然后理清楚之间的跳转关系，以及注意嵌套`while`结构的处理（栈结构）

## 遇到的问题

### 更换`llvm`版本

可以参考下面的[命令](https://apt.llvm.org)

![](https://my-picture-repo.obs.cn-east-3.myhuaweicloud.com/my-blog-imgs/image-20230111122932432.png)

可会出现下面的错误

![](https://my-picture-repo.obs.cn-east-3.myhuaweicloud.com/my-blog-imgs/image-20230111123123981.png)

按照提示输入命令即可解决。

![](https://my-picture-repo.obs.cn-east-3.myhuaweicloud.com/my-blog-imgs/image-20230111123221168.png)

### Bug

`OJ`上出现形如下图的报错：

![](https://my-picture-repo.obs.cn-east-3.myhuaweicloud.com/my-blog-imgs/image-20230113000157867.png)

结果发现`lab6`中有个点没有实现：**在全局普通变量没有初始化时，代码没有加上对其的初始化**（不知道为什么`lab6`没报错）

![](https://my-picture-repo.obs.cn-east-3.myhuaweicloud.com/my-blog-imgs/image-20230113000301079.png)