import com.trafficAccount.ApiApplication;
import com.trafficAccount.service.TrafficControlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiApplication.class) // 指定主应用程序类
public class ApiControllerTest {

    @MockBean
    private TrafficControlService trafficControlService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testApi1WithinLimit() throws Exception {
        when(trafficControlService.isRequestAllowed(anyString(), eq("api1"), anyInt())).thenReturn(true);

        mockMvc.perform(get("/api/api1").header("UserId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("api1 called successfully"));

    }

    @Test
    public void testApi1ExceedLimit() throws Exception {
        when(trafficControlService.isRequestAllowed(anyString(), eq("api1"), anyInt())).thenReturn(false);

        mockMvc.perform(get("/api/api1").header("UserId", "user1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Rate limit exceeded for api1"));
    }

    // Similar tests for api2 and api3 can be written here
    @Test
    public void testApi2WithinLimit() throws Exception {
        when(trafficControlService.isRequestAllowed(anyString(), eq("api2"), anyInt())).thenReturn(true);

        mockMvc.perform(post("/api/api2").header("UserId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("api2 called successfully"));
    }

    @Test
    public void testApi2ExceedLimit() throws Exception {
        when(trafficControlService.isRequestAllowed(anyString(), eq("api2"), anyInt())).thenReturn(false);

        mockMvc.perform(post("/api/api2").header("UserId", "user1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Rate limit exceeded for api2"));
    }

    @Test
    public void testApi3WithinLimit() throws Exception {
        when(trafficControlService.isRequestAllowed(anyString(), eq("api3"), anyInt())).thenReturn(true);

        mockMvc.perform(put("/api/api3").header("UserId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("api3 called successfully"));
    }

    @Test
    public void testApi3ExceedLimit() throws Exception {
        when(trafficControlService.isRequestAllowed(anyString(), eq("api3"), anyInt())).thenReturn(false);

        mockMvc.perform(put("/api/api3").header("UserId", "user1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Rate limit exceeded for api3"));
    }
}
