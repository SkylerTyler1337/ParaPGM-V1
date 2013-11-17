package me.parapenguin.overcast.scrimmage.map.extras.projectile;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

 public class ProjectileListener implements Listener
 {
       protected final Class<? extends Entity> cls;
       protected final float velocityMod;

   public ProjectileListener(Class<? extends Entity> cls, float velocityMod) {
     this.cls = cls;
     this.velocityMod = velocityMod;
     enable();
   }

   public void enable()
   {
     Scrimmage.getInstance().registerEvents(this);
   }

   @EventHandler(ignoreCancelled=true)
   public void changeBowProjectile(EntityShootBowEvent event) {
     World world = event.getEntity().getWorld();
     Projectile oldEntity = (Projectile)event.getProjectile();

     Entity newProjectile = world.spawn(oldEntity.getLocation(), this.cls);

     newProjectile.setVelocity(oldEntity.getVelocity());
     newProjectile.setFallDistance(oldEntity.getFallDistance());

     if ((newProjectile instanceof Projectile)) {
       ((Projectile)newProjectile).setShooter(oldEntity.getShooter());
       ((Projectile)newProjectile).setBounce(oldEntity.doesBounce());
     }

     Vector vel = oldEntity.getVelocity().multiply(this.velocityMod);

     float velocityMultiplier = (float)Math.sqrt(vel.getX() * vel.getX() + vel.getY() * vel.getY() + vel.getZ() * vel.getZ());
     int damage = (int)Math.ceil(velocityMultiplier * 2.0D);

     newProjectile.setMetadata("damage", new FixedMetadataValue(Scrimmage.getInstance(), Integer.valueOf(damage)));

     event.setProjectile(newProjectile);
   }

   @EventHandler(ignoreCancelled=true)
   public void fixEntityDamage(EntityDamageByEntityEvent event) {
     if (event.getDamager().hasMetadata("damage"))
       event.setDamage(((MetadataValue)event.getDamager().getMetadata("damage").get(0)).asInt());
   }
 }
