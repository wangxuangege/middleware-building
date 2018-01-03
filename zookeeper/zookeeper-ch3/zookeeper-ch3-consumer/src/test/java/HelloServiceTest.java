import com.wx.zookeeper.myrpc.HelloService;
import com.wx.zookeeper.myrpc.core.RpcProxy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author xinquan.huangxq
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class HelloServiceTest {

    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void sayHello() throws Exception {
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.sayHello("world");
        Assert.assertEquals("hello,world", result);
    }

}