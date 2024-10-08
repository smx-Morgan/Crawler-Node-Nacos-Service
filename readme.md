# 简易分布式爬虫系统实现

项目名称：【实践四】简易分布式爬虫系统实现

小组名称：小镇数据家

奖项：移形换影奖

小组成员：宋孟欣、刘净圆、李一卓、张敬涵、林新祺、郭宛怡
## 一、目录：

1. 项目介绍
2. 项目内容

     (1):项目分工

     (2):系统设计

         - 系统架构图及说明
         - 系统核心模块说明
         - 系统中涉及的分布式思想
    
     (3):项目测试及成果

         - 运行结果
         - Demo演示
         - 实现功能
         - 核心代码实现说明
         - 项目代码仓库
 3. 项目总结
## 二、项目详情

### 1.项目介绍：

随着互联网高速发展，海量信息爬取用于不同场景中，分布式爬虫系统广泛应用于大型爬虫项目中，面对海量待抓取网页，采用分布式架构，能在较短时间内完成抓取工作，多台机器同时爬取数据获取效率更高。通过搭建完全分布式系统、解析json获取url种子库并采用流式处理将其上传至基于HDFS分布式文件存储系统的Hbase数据库。

技术栈:Zookeeper，Hbase，SpringBoot，Redis，feign，Nacos，WebMagic


项目代码仓库：https://github.com/SmallTownCrawler/Crawler-Node-Nacos-Service

### 2.项目内容

#### (1):项目分工：

        宋孟欣（组长）：系统架构设计+统筹全组分工合作、进度安排+完全分布式环境的集群部署+基础项目环境搭建+redis的实现（config以及存取接口+项目的整合、测试、完善）
        林新祺：系统架构设计+完全分布式环境的集群部署+调度系统（Nacos）、RPC调用（Feign）、爬虫节点（SpringBoot微服务）3大模块的完整设计与实现
                   +Redis存取URL库的实现+整合节点与其他模块+部署
        刘净圆：flink模块（时间原因未能部署）+完全分布式环境的集群部署+调度系统（初版）+zookeeper监控系统（hbase）
        郭宛怡：HBase API的编写+表格的设计+完成爬虫数据的存储+实现定时删除已存储的数据
        张敬涵：爬虫节点（WebMagci框架）+对外封装接口+IP代理+节点功能测试
        李一卓：协调组内分工进度、对接集群与爬虫节点+图类绘制+文档撰写

#### (2):系统设计：

***a. 系统架构图及说明(架构图)***

分布式爬虫系统

![架构图.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/245653828ffd4f8785d7f98eb8ef1a5f~tplv-k3u1fbpfcp-watermark.image?)

爬虫节点

![爬虫节点.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3dba4c0c4108439ba1bba0232b1fc536~tplv-k3u1fbpfcp-watermark.image?)
***b.    系统核心模块说明***

- URL调度系统：

     URL调度系统是实现整个爬虫系统分布式的桥梁与关键，正是通过URL调度系统的使用，才使得整个爬虫系统可以较为高效（Redis作为存储）随机地获取url，并实现整个系统的分布式。
     
     URL仓库其实是Redis仓库，即在我们的系统中使用Redis来保存url地址列表，只要保存了url是唯一的，这样不管爬虫程序有多少个，最终保存下来的数据都是只有唯一一份、而不会重复，是通过这样来实现分布式的。
   
   + 调度系统
   
     项目中我们使用的是Feign声明式远程调用实现URL分发给爬虫节点。爬虫服务注册到Nacos后，Nacos会负责管理爬虫服务，同时使用心跳机制确认爬虫节点的存活。而分发系统通过Feign声明调用爬虫服务，Feign会找Nacos要所有的爬虫服务，然后根据负载均衡规则选出一个爬虫服务，将URL分发给此爬虫。
   
   + 爬虫系统
   
    分布式爬虫系统是运行于机器集群之上的，集群中每一个节点都是一个集中式爬虫，其工作原理与集中式爬虫系统的工作原理相同。但集中式爬虫在分布式爬虫系统中是由一个主节点控制来协同工作的。而分布式爬虫系统则要求多个节点协同工作，这样多个节点之间可相互通信来交互信息。
   
   爬虫系统是一个独立运行的进程，我们把我们的爬虫系统打包成jar包，然后分发到不同的节点上执行，这样并行爬取数据可以提高爬虫的效率。
   
   + IP代理
   
   加入随机IP代理主要是为了反反爬虫，因此如果有一个IP代理库，并且可以在构建http客户端时可以随机地使用不同的代理，那么对我们进行反反爬虫则会有很大的帮助。但目前无稳定ip。
   
   + 爬虫节点
   
   网页解析器就是把下载的网页中我们感兴趣的数据解析出来，并保存到某个对象中，供数据存储器进一步处理以保存到不同的持久化仓库中。并且其使用了WebMagic框架，可省略网页下载器，框架已帮助实现此功能。网页解析器在整个系统的开发中也算是比较重头戏的一个组件，功能不复杂，主要是代码比较多，针对不同的商城不同的商品，对应的解析器可能就不一样了，因此需要针对特别的商城的商品进行开发，本次因时间问题只做了京东网页的解析器，爬取了全网的手机商品数据。
   
   最终将网页解析器解析出来的数据对象进行分布式存储，保存到不同的数据存储器中。
   
- Nacos 

  一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台，实现项目不同模块之间需要服务调用时，实现服务注册与发现。Nacos 致力于帮助发现、配置和管理微服务，并提供了一组简单易用的特性集，可快速实现动态服务发现、服务配置、服务元数据及流量管理。Nacos 还可以更敏捷和容易地构建、交付和管理微服务平台，是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施。
  
-    Feign
  
     Feign就在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义，在Feign的  实现下，我们只需要创建一个接口并使用注解的方式来配置它，（类似于以前Dao接口上标注Mapper注解，现在是一个微服务接口上面标注一个Feign注解即可）即可完成对服务提供方的接口绑定，简化了使用Spring Cloud Ribbon时，自动封装服务调用客户端的开发量。

-    HDFS和Hbase的节点信息  (服务器数量不够没有办法打造真实的集群系统)


![节点信息.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/19da69a6733f40aa8371c1b9d9944f28~tplv-k3u1fbpfcp-watermark.image?)
![节点信息2.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/68f06f1921be4d309b940259a56cd53a~tplv-k3u1fbpfcp-watermark.image?)
![节点信息3.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/82259b5478e947569c7cd39f5bdf96e8~tplv-k3u1fbpfcp-watermark.image?)

- 数据库设计


![数据库.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/990e963018db45c6a17db2e27e6b67f2~tplv-k3u1fbpfcp-watermark.image?)

  在HBase中的表结构为：SKU作为行键，表格设置两个列族info1和info 2 ，info1保存spu、title、price、pic（图片地址）url字段信息，info 2 保存createtime，共设置了3个版本，存储历史数据，并且数据库的表格设置了TTL，定时删除数据，避免数据过多地占用内存。


![表结构.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/daa33594bd8a4a75bfd4424a72a477a5~tplv-k3u1fbpfcp-watermark.image?)

***c.    系统中涉及的分布式思想***

通过URL调度系统的使用，才使得整个爬虫系统可以较为高效（Redis作为存储）随机地获取url，并实现整个系统的分布式。

爬虫系统设计为分布式可多节点并爬，爬虫程序本身就可运行在不同服务器上，提高爬取效率。

存储结构：分布式文件系统 基于Hadoop的以HDFS系统

Redis、HBase均为实机部署（部署在服务器上）

#### (3):项目测试及成果

***a.运行结果：***

获取在京东上手机页面url集合：

![获取京东手机页面url集合.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c96c075311144bcca4ac48df344c06fb~tplv-k3u1fbpfcp-watermark.image?)
单个手机链接信息：

![单个手机链接信息.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/db598315df0c4875af71a03474c4b703~tplv-k3u1fbpfcp-watermark.image?)
单个页面信息：

![单个页面信息.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/70eec9355d76447084226a8ece19feff~tplv-k3u1fbpfcp-watermark.image?)

表格存储内容展示：rowKey - columnName - value

![运行结果.jpg](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/12b610af3db44677bc48333863bd6367~tplv-k3u1fbpfcp-watermark.image?)

***b.Demo演示***

[小镇数据家最终作品_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1bG41157cv?is_story_h5=false&p=1&share_from=ugc&share_medium=android&share_plat=android&share_session_id=a2adaa13-1df1-4ab2-aca9-657e5d7f65d7&share_source=WEIXIN&share_tag=s_i&timestamp=1661860025&unique_k=Jlz2j1r&vd_source=1c92b67903e6bcae70eaf5ea20d34eed)

***c.实现功能：***

基本实现所有基础功能：

     待爬取URL管理：种子URL 管理
     待爬取URL分发:URL分发要基于分布式思想进行实现，至少实现两种URL分发策略并基于此策略进行 URL 分发、与各个爬虫间保持通信及时获取爬取任务执行状态
     数据爬取:进行网页数据并行爬取下载
     数据解析:针对爬取的网页数据进行并行清洗、文本分词等
     数据存储模块:将解析好的数据按照一定格式进行存储并构建索引
     数据查询模块:可以进行所需数据的查询
     爬虫进程(或节点)监控:监控爬虫运行时的状态信息+增删节点
     随机IP 代理库:反反爬虫
     DNS 解析模块:将URL 地址转换为网站服务器对应的IP地址 

***d.核心代码实现说明***

crawler-node是爬虫节点的模块，实现了接收URL，爬取网站数据，写数据到HBase和Redis；
crawler-dispatcher是分发系统的模块，实现了分发和调度URL，存取Redis种子库；
crawler-common是通用模块，集合了一些在爬虫节点和分发系统中通用的功能和类。
crawler-node和crawler-dispatcher都依赖于crawler-common。

      crawler：
      1：hbase：
         DDL：Hbase的数据定义语言，定义命名空间创建表格、删除表格这些操作的方法集合      
         DML：Hbase的数据操纵语言，对数据进行读写操作的方法集合
      2：node节点控制-controller传进传出接口（通过（@PostMapping"/search")（@PostMapping"/item"接口传进传出)、
      3：spider：爬虫节点         



***e.项目代码仓库：***

https://github.com/SmallTownCrawler/Crawler-Node-Nacos-Service
### 3.项目总结与反思

1.在服务器足够的情况下，我们可以使用HDFS - HA架构，提高项目的高可用

2.在多节点爬虫节点进行存储的时候可以加入MQ消息中间件（Kafka，rabbitMQ）处理存入Hbase数据库时所遇到的高并发可能性

3.url部分可以采用更高级的url调度系统和url定时系统进行自动更新

4.可升级部分：zookeeper自动报警，监控报警系统的加入可以让让使用者可以主动发现节点宕机，而不是等出错后被动地发现，实际中爬虫程序可能是持续不断运行的，在多个节点上部署了爬虫程序，很有必要对节点进行监控，可以在节点出现问题时及时发现并修正。
