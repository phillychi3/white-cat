package life.whitecloud.whitecat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class util {
    public static Component broadcastPrefixedMessage(Component message) {
        Component prefixedMessage = Component.literal("[white cat]")
                .withStyle(Style.EMPTY.withColor(0xFFFF55))
                .append(Component.literal(": ").withStyle(Style.EMPTY.withColor(0xFFFFFF)))
                .append(message.copy().withStyle(Style.EMPTY.withColor(0xFFFFFF)));
        return prefixedMessage;
    }
}
