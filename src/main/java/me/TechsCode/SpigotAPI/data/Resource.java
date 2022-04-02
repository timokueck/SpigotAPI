package me.TechsCode.SpigotAPI.data;

import java.util.Arrays;
import java.util.List;

public enum Resource {

    INSANE_SHOPS("Insane Shops", "insane-shops.67352"),
    ULTRA_SCOREBOARDS("Ultra Scoreboards", "ultra-scoreboards.93726"),
    ULTRA_PUNISHMENTS("Ultra Punishments", "ultra-punishments.63511"),
    ULTRA_CUSTOMIZER("Ultra Customizer", "ultra-customizer.49330"),
    ULTRA_PERMISSIONS("Ultra Permissions", "ultra-permissions.42678"),
    ULTRA_ECONOMY("Ultra Economy", "ultra-economy.83374"),
    ULTRA_REGIONS("Ultra Regions", "ultra-regions.58317"),
    ULTRA_MOTD("Ultra Motd", "ultra-motd.100883");

    private final String Name, Id;

    Resource(String Name, String Id){
        this.Name = Name;
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public String getId() {
        return Id;
    }

    public static Resource valueOfId(String Id){
        for (Resource resource : values()){
            if (resource.Id.equalsIgnoreCase(Id)){
                return resource;
            }
        }
        return null;
    }

    public static List<Resource> getAllResources(){
        return Arrays.asList(values());
    }

}
