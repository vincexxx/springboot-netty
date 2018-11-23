package com.vince.server.mapping;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageChannelMapping {

    public static final MessageChannelMapping INSTANCE = new MessageChannelMapping();


    /**
     * Map<ChannelId,Channel>
     */
    private Map<String, Channel> channels = new ConcurrentHashMap<>();
    /**
     * Map<DeviceNo,ChannelId>
     */
    private Map<Long, String> deviceChannel = new ConcurrentHashMap<>();
    /**
     * Map<ChannelId,DeviceNo>
     */
    private Map<String, Long> channelDevice = new ConcurrentHashMap<>();

    public synchronized void putMapping(Long deviceNo, Channel channel) {
        String channelId = channel.id().asLongText();
        String oldChannelId = deviceChannel.get(deviceNo);
        if (!StringUtils.isEmpty(oldChannelId)) {
            if (channelId != oldChannelId) {
                channelDevice.remove(oldChannelId);
                Channel ch = channels.get(oldChannelId);
                if (ch != null) {
                    ch.close();
                }
                channels.remove(oldChannelId);
                deviceChannel.remove(deviceNo);
            } else {
                return;
            }
        }
        deviceChannel.put(deviceNo, channelId);
        channelDevice.put(channelId, deviceNo);
        channels.put(channelId, channel);
    }

    public synchronized void removeChannel(Channel channel) {
        if (channel == null)
            return;
        String channelId = channel.id().asLongText();
        Long deviceNo = channelDevice.get(channelId);
        if (deviceNo != null) {
            deviceChannel.remove(deviceNo);
        }
        channelDevice.remove(channelId);
        Channel ch = channels.get(channelId);
        if (ch != null) {
            ch.close();
        }
        channels.remove(channelId);
    }

    public Channel getChannel(Long DeviceNo) {
        String channelId = deviceChannel.get(DeviceNo);
        if (!StringUtils.isEmpty(channelId)) {
            return channels.get(channelId);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("DeviceChannelMapping Dashboard");
        sb.append("<br/>");
        sb.append("ChannelDevice size \t");
        sb.append(channelDevice.size());
        sb.append("<br/>");
        sb.append("DeviceChannel size \t");
        sb.append(deviceChannel.size());
        sb.append("<br/>");
        sb.append("Channels size \t");
        sb.append(channels.size());
        sb.append("<br/>");
        deviceChannel.forEach((k, v) -> {
            Channel channel = channels.get(v);
            sb.append(k);
            sb.append('\t');
            sb.append(v);
            sb.append('\t');
            sb.append(channel.isActive());
            sb.append('\t');
            sb.append(channel.remoteAddress());
            sb.append("<br/>");
        });
        sb.append('\n');
        return sb.toString();
    }
}
