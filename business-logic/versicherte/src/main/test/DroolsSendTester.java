import org.apache.logging.log4j.LogManager;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.claninfo.clanng.web.connect.DroolsSend;
import ch.claninfo.common.connect.BeanReceiver;
import ch.claninfo.common.connect.CommException;
import ch.claninfo.common.connect.Method;
import ch.claninfo.common.connect.MsgTarget;
import ch.claninfo.pvclan.bo.VersLohnBo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class DroolsSendTester {

	@AfterClass
	public static void gracefulExit() {
		LogManager.shutdown();

	}

	@Test
	public void simpleFetchTest() throws CommException {
		DroolsSend droolsSender = new DroolsSend(null, null);

		BeanReceiver<VersLohnBo> beanReceiver = new BeanReceiver<>(VersLohnBo.class);

		droolsSender.fetch(MsgTarget.PROD, "0", "Tx 1", "pvclan", "VersLohn", "pVSNUM;pVSLGDAT;", "pVSNUM='157185'", "", null, 2, 10, 10, beanReceiver);
	}

	@Test
	public void simpleInsertTest() throws CommException {
		DroolsSend droolsSend = new DroolsSend(null, null);

		BeanReceiver<VersLohnBo> beanReceiver = new BeanReceiver<>(VersLohnBo.class);
		droolsSend.startParlist("pvclan", "VersLohn", Method.INS, beanReceiver);
		droolsSend.param("pVSNUM", 153363, false, false);
	}

	@Test
	public void simpleUpdate() {}
}
