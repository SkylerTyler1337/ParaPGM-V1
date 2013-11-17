package me.parapenguin.overcast.scrimmage.map.extras.projectile;

import me.parapenguin.overcast.scrimmage.Scrimmage;
import me.parapenguin.overcast.scrimmage.ServerLog;
import me.parapenguin.overcast.scrimmage.map.MapLoader;
import org.bukkit.entity.Entity;
import org.dom4j.Document;
import org.dom4j.Element;


 public class ModifyBowProjectileModule {
       protected final Class<? extends Entity> cls;
       protected final float velocityMod;

   public ModifyBowProjectileModule(Class<? extends Entity> cls, float velocityMod)
   {
     this.cls = cls;
     this.velocityMod = velocityMod;
     createListener();
   }

   public ProjectileListener createListener()
   {
     return new ProjectileListener(this.cls, this.velocityMod);
   }

   public static ModifyBowProjectileModule parse(Document doc)
   {
     boolean changed = false;
     String projectile = "Arrow";
     float velocityMod = 1.0F;

       Element root = Scrimmage.getMap().getLoader().getDoc().getRootElement();

       for(Element parent : MapLoader.getElements(root, "modifybowprojectile")) {
           for ( Element projectileelement : MapLoader.getElements(parent, "projectile") ) {
               if (projectileelement != null) {
                   projectile = projectileelement.getStringValue();
                   changed = true;
               } }
               for ( Element velocityelement : MapLoader.getElements(parent, "velocity") ) {
               if (velocityelement != null) {
                  try {
                      velocityMod = Float.parseFloat(velocityelement.getStringValue());
                      changed = true;
               } catch (NumberFormatException e) {
                   e.printStackTrace();
               }
       }}}

     if (changed) {
         ServerLog.info("Changed Projectile to: " + projectile + " with velocity: " + velocityMod + "!");
       try {
         return new ModifyBowProjectileModule(Class.forName("org.bukkit.entity." + projectile).asSubclass(Entity.class), velocityMod);
       } catch (ClassNotFoundException e) {
         throw new IllegalArgumentException("Could not find class org.bukkit.entity." + projectile);
       }
     }
     return null;
   }
 }
