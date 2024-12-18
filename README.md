题目:请用 Java/spring及相关开源 Lib 开发基于(Apache kaka或 redis或者 mongodb或者 jvm内存)的流量计费系统。流量控制要求可以达到按每分钟控制，如用户1，配置每分钟允许访问某api一百次
要求:写相关 springboot test 测试用例，mock 高并发请求.api1(get)，api2(post)，api3(put)，user1每秒实际随机请求(api1,2,3)500次，配置 user1每分钟只能请求(api1,2,3)10000次,以此类推user2,user3,user4 做类似的边缘测试。测试结果需要体现流量控制效果，低于流量控制阈值运行请求，频率高于流量控制值返回友好信息。性能要好，响应速度要快，流量统计过程不能影响api数据请求时效。对测试过程对流量统计逻辑所占用的时间监测。程序能本地运行，能运行springboot test.                                     加分项:如能结合算法和限流，以及流式计算(kafka)，做到实时计算。
Jdk11 以上

最后结果：
使用redis+lua脚本实现限流
HighConcurrencyTest测试类中，使用了testHighConcurrency()方法，模拟两个用户并发执行任务，该任务每秒随机请求api1\2\3接口500次，持续1分钟，最后测试结果如下：
User user1: API1 Success: 10000, API1 Failure: 330, API2 Success: 9942, API2 Failure: 0, API3 Success: 10000, API3 Failure: 228
User user2: API1 Success: 10000, API1 Failure: 141, API2 Success: 10000, API2 Failure: 126, API3 Success: 10000, API3 Failure: 233
