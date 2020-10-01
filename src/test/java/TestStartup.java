import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import io.github.sirnik.daduels.DADuels;
import org.bukkit.GameMode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

public class TestStartup {
    private ServerMock server;
    private DADuels plugin;
    private PlayerMock mockedPlayer;

    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(DADuels.class);
        mockedPlayer = new PlayerMock(server, "testPlayer", UUID.randomUUID());
    }

    @Test
    public void TestPlayer() {
        //Given
        //When
        //Then
        Assert.assertEquals(mockedPlayer.getGameMode(), GameMode.SURVIVAL);
    }

    @After
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
