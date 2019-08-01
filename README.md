# netty-in-action
阅读netty-in-action的学习历程，定一个目标两个月？
## 学习资料
- [netty-in-action-cn](https://github.com/ReactivePlatform/netty-in-action-cn)
- [netty-in-action](https://github.com/normanmaurer/netty-in-action)

## 源码学习
### 服务端的Socket在哪里进行的初始化？
1. 初始化Channel，如NioServerSocketChannel.class
```
ServerBootstrap#bind()
-> initAndRegister()
    // 初始化 .channel() 中传入的 channel.class, 如 NioServerSocketChannel.class
    // 见下方NioServerSocketChannel的无参构造函数
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
    // 调用EventLoopGroup中的register
    -> register()
        -> EventLoopGroup#register(Channel channel)
            -> EventLoopGroup#register(ChannelPromise promise);
                // 最终会落在channel的register方法
                -> AbstractChannel#register(eventLoop, promise)
                    // 赋值ParntEventLoop
                    -> this.eventLoop = eventLoop;
                    -> register0()
                        // Nio Epoll等不同的操作
                        // Nio：调用jdk底层进行注册
                        -> doRegister()
                        // 对应handler中的add
                        -> invokeHandlerAddedIfNeeded()
                        // 对应handler中的registered
                        -> fireChannelRegistered()
        
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
3. doBind0()
```
-> AbstractChannel#AbstractUnsafe#bind()
    // jdk的绑定
    -> doBind()
        -> javaChannel().bind()
    // 事件的传播
    -> pipeline.fireChannelActive()
        -> HeadContext#channelActive(ChannelHandlerContext ctx)
            -> ctx.fireChannelActive();
            -> 
```
### 服务端的Socket在哪里进行的accept连接？
```
/**
  * AbstractNioChannel#doBeginRead
  */
@Override
protected void doBeginRead() throws Exception {
    // Channel.read() or ChannelHandlerContext.read() was called
    final SelectionKey selectionKey = this.selectionKey;
    if (!selectionKey.isValid()) {
        return;
    }

    readPending = true;

    final int interestOps = selectionKey.interestOps();
    // 这里的readInterestOp就是NioServerSocketChannel中构造注入的SelectionKey.OP_ACCEPT
    if ((interestOps & readInterestOp) == 0) {
        selectionKey.interestOps(interestOps | readInterestOp);
    }
}

```