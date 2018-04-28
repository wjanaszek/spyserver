import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestSpyServer {

    private String message = "Server started";
    private SpyServer spyServer = new SpyServer();

    @Test
    public void testStartServer() {
        assertEquals(message, spyServer.startServer());
    }
}