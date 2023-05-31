import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Fenetre extends JFrame {
    private JLabel cityLabel;
    private JLabel tempLabel;
    private JLabel humidityLabel;
    private JLabel windSpeedLabel;
    private JLabel dateLabel;
    private JLabel weatherIconLabel;
    private JTextField cityTextField;
    private JButton searchButton;
    private DataBase database;
    private WeatherIconManager weatherIconManager;

    private static final String API_KEY = "59d15170100aeff9d78d715578e199ac";
    private static final int WEATHER_ICON_SIZE = 50; // Taille des icônes en pixels

    public Fenetre() throws SQLException, ClassNotFoundException {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 10, 5, 10);

        cityLabel = new JLabel();
        tempLabel = new JLabel();
        humidityLabel = new JLabel();
        windSpeedLabel = new JLabel();
        dateLabel = new JLabel();
        weatherIconLabel = new JLabel();
        cityTextField = new JTextField(30);
        searchButton = new JButton("Search");

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(new JLabel("City:"), constraints);

        constraints.gridx = 1;
        add(cityLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(new JLabel("Temperature:"), constraints);

        constraints.gridx = 1;
        add(tempLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        add(new JLabel("Humidity:"), constraints);

        constraints.gridx = 1;
        add(humidityLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(new JLabel("Wind Speed:"), constraints);

        constraints.gridx = 1;
        add(windSpeedLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        add(new JLabel("Date:"), constraints);

        constraints.gridx = 1;
        add(dateLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        add(new JLabel("Weather Icon:"), constraints);

        constraints.gridx = 1;
        add(weatherIconLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        add(new JLabel("Enter city name:"), constraints);

        constraints.gridx = 1;
        add(cityTextField, constraints);

        constraints.gridx = 2;
        constraints.gridy = 6;
        add(searchButton, constraints);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = cityTextField.getText();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                }
            }
        });

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        database = new DataBase("weather");
        weatherIconManager = new WeatherIconManager();
    }

    private void getWeatherData(String city) {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");

                JSONObject wind = jsonObject.getJSONObject("wind");
                double windSpeed = wind.getDouble("speed");

                long timestamp = jsonObject.getLong("dt");

                City cityObj = new City(city, temperature, humidity, windSpeed, timestamp, "");

                updateCityData(cityObj);

                // Vérification si la ville existe déjà dans la base de données
                PreparedStatement selectStatement = database.getConnection().prepareStatement("SELECT * FROM city_data WHERE city = ?");
                selectStatement.setString(1, city);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    // Si la ville existe déjà, on met à jour les données
                    database.updateCityData(cityObj);
                } else {
                    // Si la ville n'existe pas, on l'ajoute à la base de données
                    database.insertCityData(cityObj);
                }

                resultSet.close();
                selectStatement.close();
            } else {
                JOptionPane.showMessageDialog(this, "Error: City not found!");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCityData(City city) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        cityLabel.setText(city.getCityName());
        tempLabel.setText(decimalFormat.format(city.getTemperature()) + " °C");
        humidityLabel.setText(decimalFormat.format(city.getHumidity()) + " %");
        windSpeedLabel.setText(decimalFormat.format(city.getWindSpeed()) + " m/s");

        Date date = new Date(city.getTimestamp() * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        dateLabel.setText(sdf.format(date));

        ImageIcon weatherIcon = city.getWeatherIcon();
        // Redimensionnement de l'icône
        Image scaledImage = weatherIcon.getImage().getScaledInstance(WEATHER_ICON_SIZE, WEATHER_ICON_SIZE, Image.SCALE_SMOOTH);
        weatherIconLabel.setIcon(new ImageIcon(scaledImage));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new Fenetre();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
