import com.trafficAccount.ApiApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = ApiApplication.class) // 指定主应用程序类
@AutoConfigureMockMvc
public class HighConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(HighConcurrencyTest.class);

    @Autowired
    private MockMvc mockMvc;

    private static final int REQUESTS_PER_SECOND = 500;
    private static final int TOTAL_USERS = 2;

    private ScheduledExecutorService scheduler;

    // ConcurrentHashMap to store request statistics
    private final ConcurrentHashMap<String, Map<String, AtomicInteger>> requestStats = new ConcurrentHashMap<>();

    @BeforeEach
    public void setUp() {
        scheduler = Executors.newScheduledThreadPool(TOTAL_USERS * REQUESTS_PER_SECOND * 20); // 增加线程池大小
        requestStats.clear(); // 清空统计信息
        for (int user = 1; user <= TOTAL_USERS; user++) {
            String userId = "user" + user;
            requestStats.putIfAbsent(userId, new HashMap<>());
            requestStats.get(userId).put("api1_success", new AtomicInteger(0));
            requestStats.get(userId).put("api1_failure", new AtomicInteger(0));
            requestStats.get(userId).put("api2_success", new AtomicInteger(0));
            requestStats.get(userId).put("api2_failure", new AtomicInteger(0));
            requestStats.get(userId).put("api3_success", new AtomicInteger(0));
            requestStats.get(userId).put("api3_failure", new AtomicInteger(0));

        }
    }

    @AfterEach
    public void tearDown() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
        printRequestStatistics();
    }

    @Test
    public void testHighConcurrency() throws InterruptedException {
        Random random = new Random();

        for (int user = 1; user <= TOTAL_USERS; user++) {
            final int userId = user;
            scheduler.scheduleAtFixedRate(() -> {
                for (int i = 0; i < REQUESTS_PER_SECOND; i++) {
                    final int apiNumber = random.nextInt(3) + 1; // Randomly choose between api1, api2, api3
                    try {
                        String apiUrl = "/api/api" + apiNumber;
                        String response = "";
                        switch (apiNumber) {
                            case 1:
                                response = mockMvc.perform(get(apiUrl).header("UserId", "user" + userId))
                                        .andReturn().getResponse().getContentAsString();
                                updateStatistics("user" + userId, "api1", response);
                                break;
                            case 2:
                                response = mockMvc.perform(post(apiUrl).header("UserId", "user" + userId))
                                        .andReturn().getResponse().getContentAsString();
                                updateStatistics("user" + userId, "api2", response);
                                break;
                            case 3:
                                response = mockMvc.perform(put(apiUrl).header("UserId", "user" + userId))
                                        .andReturn().getResponse().getContentAsString();
                                updateStatistics("user" + userId, "api3", response);
                                break;
                        }
                    } catch (Exception e) {
                        logger.error("User {} request to API{} failed: {}", userId, apiNumber, e.getMessage());
                        updateStatistics("user" + userId, "api" + apiNumber, "failure");
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        // Let the test run for a minute to simulate the high concurrency
        Thread.sleep(60 * 1000);

    }

    private void updateStatistics(String userId, String apiName, String response) {
        Map<String, AtomicInteger> stats = requestStats.get(userId);
        if (response.contains(apiName+" called successfully")) {
            stats.get(apiName + "_success").incrementAndGet();
        } else {
            stats.get(apiName + "_failure").incrementAndGet();
        }
    }

    private void printRequestStatistics() {
        logger.info("Request Statistics:");
        for (Map.Entry<String, Map<String, AtomicInteger>> entry : requestStats.entrySet()) {
            String userId = entry.getKey();
            Map<String, AtomicInteger> stats = entry.getValue();
            logger.info("User {}: API1 Success: {}, API1 Failure: {}, API2 Success: {}, API2 Failure: {}, API3 Success: {}, API3 Failure: {}",
                    userId,
                    stats.get("api1_success").get(),
                    stats.get("api1_failure").get(),
                    stats.get("api2_success").get(),
                    stats.get("api2_failure").get(),
                    stats.get("api3_success").get(),
                    stats.get("api3_failure").get()
            );
        }
    }

}

