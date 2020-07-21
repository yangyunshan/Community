package com.example.community;

import com.example.community.dao.DiscussPostMapper;
import com.example.community.dao.UserMapper;
import com.example.community.dao.elasticsearch.DiscussPostRepository;
import com.example.community.entity.DiscussPost;
import com.example.community.entity.User;
import com.example.community.util.MailClient;
import com.example.community.util.SensitiveFilter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests {

	@Autowired
	private UserMapper userMapper;

	@Test
	public void testSelectUser() {
		User user = userMapper.selectById(101);
		System.out.println(user);
	}

	@Autowired
	private DiscussPostMapper discussPostMapper;

	@Test
	public void testSelectDiscussPost() {
		List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0,0,10);
		for (DiscussPost discussPost : discussPosts) {
			System.out.println(discussPost);
		}

		int count = discussPostMapper.selectDiscussPostRows(0);
		System.out.println("Count: "+count);
	}

	@Autowired
	private MailClient mailClient;

	@Test
	public void testMail() {
		mailClient.sendMail("yangyunshan123@gmail.com","TEST","TestMail");
	}


	@Autowired
	private SensitiveFilter sensitiveFilter;

	@Test
	public void testSensitive() {
		String text = "赌博上厨房开票塞弗呀";
		System.out.println(sensitiveFilter.filter(text));
	}

	@Autowired
	private RedisTemplate redisTemplate;
	@Test
	public void testRedis() {
		String key = "test:count";

		redisTemplate.opsForValue().set(key, 1);

		System.out.println(redisTemplate.opsForValue().get(key));
		System.out.println(redisTemplate.opsForValue().increment(key));
		System.out.println(redisTemplate.opsForValue().decrement(key));
	}

	/**
	 * 编程式事务
	 */
	@Test
	public void testRedisTransactional() {
		Object obj = redisTemplate.execute(new SessionCallback() {
			@Override
			public Object execute(RedisOperations redisOperations) throws DataAccessException {
				String key = "test:tx";
				redisOperations.multi();

				redisOperations.opsForSet().add(key, "111");
				redisOperations.opsForSet().add(key, "222");
				redisOperations.opsForSet().add(key, "333");

				System.out.println(redisOperations.opsForSet().members(key));

				return redisOperations.exec();
			}
		});
		System.out.println(obj);
	}

	/**
	 * Kafka测试
	 */
//	@Autowired
//	private EventProducer eventProducer;

//	@Test
//	public void testKafka() {
//		eventProducer.fireEvent("test", "goodluck");
//	}

	/**
	 * 测试Elasticsearch
	 */
	@Autowired
	private DiscussPostRepository discussPostRepository;

	@Test
	public void testES() {
		discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
	}
}

