import javax.swing.ImageIcon;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class City {
    private String cityName;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private long timestamp;
    private String weatherIconCode;


    // Définissez un dictionnaire (Map) pour associer les conditions météorologiques aux icônes
    private static final Map<String, ImageIcon> weatherIcons = new HashMap<>();

    static {
        // Ajoutez les icônes météorologiques correspondantes ici
        weatherIcons.put("sunny", new ImageIcon("C:\\Users\\user\\Devoir6_Java\\src\\sunny.png"));
        weatherIcons.put("cloudy", new ImageIcon("C:\\Users\\user\\Devoir6_Java\\src\\cloudy.png"));
        weatherIcons.put("rain", new ImageIcon("C:\\Users\\user\\Devoir6_Java\\src\\rain.png"));
        weatherIcons.put("snow", new ImageIcon("C:\\Users\\user\\Devoir6_Java\\src\\snow.jpg"));
        weatherIcons.put("windy", new ImageIcon("C:\\Users\\user\\Devoir6_Java\\src\\windy.jpg"));
    }

    public City(String cityName, double temperature, double humidity, double windSpeed, long timestamp, String weatherIconCode) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.timestamp = timestamp;
        this.weatherIconCode = weatherIconCode;
    }

    //Constructeur vide
    public City() {

    }

    // Getters and setters...

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }



    public String getWeatherIconCode() {
        //conditions pour savoir quel icon afficher
        if (temperature == 30) {
            return "sunny";
        } else if (temperature >= 20) {
            return "cloudy";
        } else if (humidity >= 70) {
            return "rain";
        } else if (temperature <= 0) {
            return "snow";
        } else {
            return "windy";
        }
    }

    public ImageIcon getWeatherIcon() {
        String iconCode = getWeatherIconCode();
        return weatherIcons.get(iconCode);
    }

    public String getDate() {
        // On cree un objet `LocalDateTime` à partir du timestamp
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());

        // On formatte la date selon le pattern souhaité (par exemple : "yyyy-MM-dd HH:mm:ss")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Ce code Retourne la date formatée sous forme de chaîne de caractères
        return dateTime.format(formatter);
    }
}