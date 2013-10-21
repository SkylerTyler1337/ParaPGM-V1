package me.parapenguin.overcast.scrimmage.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.parapenguin.overcast.scrimmage.player.Client;
import net.minecraft.server.v1_6_R3.Packet63WorldParticles;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class Particle {
	
	@Getter String packetName;
	@Getter ParticleType type;
	@Getter @Setter Location location;
	
	@Getter int id;
	@Getter int data;

	@Getter @Setter int speed;	
	@Getter @Setter int count;
	
	public Particle(ParticleType type, Location location, int speed, int count) {
		this.type = type;
		this.location = location;
		this.speed = speed;
		this.count = count;
		
		this.packetName = type.getPacketName();
	}
	
	public Particle(ParticleType type, Location location) {
		this(type, location, 0, 0);
	}
	
	public void setID(int id) {
		this.id = id;
		
		if(this.packetName.contains("%id%"))
			this.packetName.replace("%id%", "" + id);
	}
	
	public void setData(int data) {
		this.data = data;
		
		if(this.packetName.contains("%data%"))
			this.packetName.replace("%data%", "" + data);
	}
	
	public boolean sendParticle(Client client) {
		Packet63WorldParticles packet;
		
		try {
			packet = getPacket();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		CraftPlayer craftPlayer = (CraftPlayer) client.getPlayer();
		craftPlayer.getHandle().playerConnection.sendPacket(packet);
		return true;
	}
	
	public boolean sendParticle(int radius) {
		List<Client> clients = new ArrayList<Client>();
		for(Entity entity : location.getWorld().getEntities())
			if(entity instanceof Player && entity.getLocation().distance(location) < radius)
    				clients.add(Client.getClient((Player) entity));
		
		return sendParticle(clients);
	}
	
	public boolean sendParticle(List<Client> clients) {
		boolean all = true;
		for(Client client : clients)
			if(!sendParticle(client))
				all = false;
		
		return all;
	}
	
	private Packet63WorldParticles getPacket() throws Exception {
		Packet63WorldParticles packet = new Packet63WorldParticles();
		setValue(packet, "a", getPacketName());
		setValue(packet, "b", (float) location.getX());
		setValue(packet, "c", (float) location.getY());
		setValue(packet, "d", (float) location.getZ());
		setValue(packet, "e", 0);
		setValue(packet, "f", 0);
		setValue(packet, "g", 0);
		setValue(packet, "h", speed);
		setValue(packet, "i", count);
		return packet;
	}
	
	/**
	 * Reflection to set the values of the packet
	 * @param instance
	 * @param fieldName
	 * @param value
	 * @throws Exception
	 */
	private static void setValue(Object instance, String fieldName, Object value) throws Exception {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(instance, value);
	}
	
}
