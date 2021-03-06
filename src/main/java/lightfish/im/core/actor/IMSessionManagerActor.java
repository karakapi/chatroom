package lightfish.im.core.actor;

import akka.actor.UntypedActor;
import lightfish.im.core.actorMsg.IMSession;
import lightfish.im.core.actorMsg.SessionMsg;
import lightfish.im.kit.MySubscribe;
import lightfish.im.kit.RedisSessionDAO;
import lightfish.im.kit.SentinelJedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by karak on 16-3-14.
 */
@Named("IMSessionManagerActor")
@Scope("prototype")//singleton
public class IMSessionManagerActor extends UntypedActor {
    private static final Logger log = LoggerFactory.getLogger(IMSessionManagerActor.class);
    Pattern pattern;
    Set<InetSocketAddress> addresses;

    MySubscribe subscribe;
    final RedisSessionDAO redis;

    @Inject
    public IMSessionManagerActor(@Named("redis") RedisSessionDAO redis) {
        this.redis = redis;
        pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        addresses = new HashSet<InetSocketAddress>();
        subscribe = new MySubscribe();
    }


    @Override
    public void onReceive(Object message) throws Exception {
        if (message.equals("session")) {

            SentinelJedisUtil.getJedis().psubscribe(subscribe, "*");
            return;
        }
        if (message instanceof SessionMsg) {


            redis.update(((SessionMsg) message).getId());
        } else {
            unhandled(message);
        }


    }


}
