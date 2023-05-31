import java.sql.*;

class DataBase {
    private Connection connection;

    public DataBase(String databaseName) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
        createTable(); // Appel à la méthode createTable()
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS city_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "city TEXT," +
                "temperature REAL," +
                "humidity REAL," +
                "wind_speed REAL," +
                "date TEXT" +
                ")";
        Statement statement = connection.createStatement();
        statement.execute(sql);
        statement.close();
    }

    public void insertCityData(City city) throws SQLException {
        String sql = "INSERT INTO city_data (city, temperature, humidity, wind_speed, date) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, city.getCityName());
        preparedStatement.setDouble(2, city.getTemperature());
        preparedStatement.setDouble(3, city.getHumidity());
        preparedStatement.setDouble(4, city.getWindSpeed());
        preparedStatement.setString(5, city.getDate());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void updateCityData(City city) throws SQLException {
        PreparedStatement selectStatement = connection.prepareStatement("SELECT * FROM city_data WHERE city = ?");
        selectStatement.setString(1, city.getCityName());
        ResultSet resultSet = selectStatement.executeQuery();

        if (resultSet.next()) {
            // Si la ville existe déjà dans la base de données, on met à jour les données existantes
            int id = resultSet.getInt("id");
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE city_data SET temperature = ?, humidity = ?, wind_speed = ?, date = ? WHERE id = ?");
            updateStatement.setDouble(1, city.getTemperature());
            updateStatement.setDouble(2, city.getHumidity());
            updateStatement.setDouble(3, city.getWindSpeed());
            updateStatement.setString(4, city.getDate());
            updateStatement.setInt(5, id);
            updateStatement.executeUpdate();
            updateStatement.close();
        } else {
            // Si la ville n'existe pas dans la base de données, on insère de nouvelles données
            insertCityData(city);
        }

        resultSet.close();
        selectStatement.close();
    }

    public void insertData(String cityName, double temperature, double humidity, double windSpeed, String date) throws SQLException {
        String sql = "INSERT INTO city_data (city, temperature, humidity, wind_speed, date) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, cityName);
        preparedStatement.setDouble(2, temperature);
        preparedStatement.setDouble(3, humidity);
        preparedStatement.setDouble(4, windSpeed);
        preparedStatement.setString(5, date);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    public Connection getConnection() {
        return connection;
    }

}
