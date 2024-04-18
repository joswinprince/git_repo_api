package custom.git.api;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GitHubRepoCreator {

    private static final String GITHUB_API_URL = "https://api.github.com/user/repos";
    private String authToken;
    
    
    public GitHubRepoCreator(String authToken) {
		this.authToken = authToken;

        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        // Format the date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String formattedDateTime = currentDateTime.format(formatter);
        // Append the formatted date and time to the repository name
        String repoName = "repo-" + formattedDateTime;

        createGitHubRepo(repoName, false, authToken); // Set to true to create a private repository
        //createFolderAndFiles(repoName);
        createFile("index.html", "<html></html>", "main", "Creating file",authToken);
    
	}

	public static void main(String[] args) {}

    public static boolean createGitHubRepo(String repoName, boolean isPrivate,String authToken) {
    	boolean status=false;
        String jsonBody = "{\"name\":\"" + repoName + "\", \"private\": " + isPrivate + "}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_API_URL))
                .header("Authorization", "token " + authToken)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
            	status = true;
                System.out.println("Repository '" + repoName + "' created successfully on GitHub.");
                return status;
            } else {
                System.out.println("Failed to create repository '" + repoName + "'. Status code: " + response.statusCode());
                System.out.println("Response: " + response.body());
                return status;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
		
		return status;
		
    }
    
    public static void createFolderAndFiles(String repoName) {
        try {
            // Create a directory for the repository
            Path repoDir = Paths.get(repoName);
            Files.createDirectory(repoDir);

            // Create files inside the directory
            String[] fileNames = {"file1.txt", "file2.txt"};
            for (String fileName : fileNames) {
                Path filePath = Paths.get(repoName, fileName);
                Files.createFile(filePath);
            }

            System.out.println("Folder and files created under the repository '" + repoName + "'.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void createFile(String fileName, String content, String branch, String commitMessage,String authToken) {
        String apiUrl = GITHUB_API_URL + "/joswin-hephsibahtech" +"" +fileName;
        String base64Content = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));

        String jsonBody = "{\"message\": \"" + commitMessage + "\", \"content\": \"" + base64Content + "\", \"branch\": \"" + branch + "\"}";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "token " + authToken)
                .header("Accept", "application/vnd.github.v3+json")
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                System.out.println("File '" + fileName + "' created successfully.");
            } else {
                System.out.println("Failed to create file '" + fileName + "'. Status code: " + response.statusCode());
                System.out.println("Response: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
