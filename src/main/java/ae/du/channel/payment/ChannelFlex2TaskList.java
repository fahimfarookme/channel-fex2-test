package ae.du.channel.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ae.du.channel.payment.api.session.Request;

@SpringBootApplication
@EnableConfigurationProperties(Configuration.class)
public class ChannelFlex2TaskList implements CommandLineRunner {

	public static void main(final String[] args) {
		SpringApplication.run(ChannelFlex2TaskList.class, args);
//		System.out.println(Util.newDate());
	}

	@Autowired
	private Request session;

	@Override
	public void run(final String... args) throws Exception {
		this.session.call();
	}
}
