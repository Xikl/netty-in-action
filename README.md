# netty-in-action
阅读netty-in-action的学习历程，定一个目标两个月？
## 学习资料
- [netty-in-action-cn](https://github.com/ReactivePlatform/netty-in-action-cn)
- [netty-in-action](https://github.com/normanmaurer/netty-in-action)

## 源码学习
### 服务端的Socket在哪里进行的初始化？
1. 初始化Channel，如NioServerSocketChannel.class
```
ServerBootstrap.bind()
-> initAndRegister()
    // 初始化 .channel() 中传入的 channel.class, 如 NioServerSocketChannel.class
    -> newChannel()  
    -> init()
        -> setChannelOptions setChannelAttrs
        // 设置为局部变量
        -> setChildOptions setChildAttrs
        // 设置用户自己的服务端handler
        -> addHandler
        // 一个保存了用户的自定义熟悉 创建一个新连接
        // ServerBootstrapAcceptor(channel, childGroup, childHandler,childOptions, childAttrs)
        -> addServerBootstrapAcceptor
        
```
2. NioServerSocketChannel的无参构造函数
```
// 这里创建了java Nio的Socket provider.openServerSocketChannel();
newSocket(DEFAULT_SELECTOR_PROVIDER))
-> NioServerSocketChannelConfig()
    // 设置pipline
    -> AbstractChannel(id, unsafe, pipline)
    // 设置了非阻塞的模式
    -> configureBlocking(false)
    
```
### 服务端的Socket在哪里进行的accept连接？