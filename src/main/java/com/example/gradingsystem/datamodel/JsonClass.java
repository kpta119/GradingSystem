package com.example.gradingsystem.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonClass {
    public JsonClass(){}

    private String getJSONFromFile() {
        String jsonText = "";
        try {
            String filename = GradingSystemData.getInstance().getFilename();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonText += line + "\n";
            }

            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonText;
    }

    public void loadTestItems() throws Exception {
        ObservableList<Test> testItems = FXCollections.observableArrayList();
        try {
            JSONParser parser = new JSONParser();
            String strJson = getJSONFromFile();
            Object object = parser.parse(strJson);
            JSONArray mainJsonArray = (JSONArray) object;
            for (int i=0; i<mainJsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) mainJsonArray.get(i);
                String dateString = (String) jsonObject.get("whenTaken");
                LocalDate dateOfTest = LocalDate.parse(dateString, GradingSystemData.getInstance().getDateFormatter());
                String nameOfTest = (String) jsonObject.get("name");
                Test newTest = new Test(nameOfTest, dateOfTest);
                JSONArray tasksOnTest = (JSONArray) jsonObject.get("tasksOnTest");
                for (int j=0; j<tasksOnTest.size(); j++){
                    JSONObject singleTask = (JSONObject) tasksOnTest.get(j);
                    int numberOfTask = ((Long) singleTask.get("numberOfTask")).intValue();
                    int maxPoints = ((Long) singleTask.get("maxPoints")).intValue();
                    String typeString = (String) singleTask.get("type");
                    TaskType type = TaskType.valueOf(typeString.toUpperCase());
                    Task newTask = new Task(numberOfTask, maxPoints, type);
                    newTest.addTask(newTask);
                }
                JSONArray studentResults = (JSONArray) jsonObject.get("studentResults");
                for (int j=0; j<studentResults.size(); j++){
                    JSONObject singleResult = (JSONObject) studentResults.get(j);
                    String studentName = (String) singleResult.get("studentName");
                    StudentResult newStudentResult = new StudentResult(studentName);
                    JSONObject allGrades = (JSONObject) singleResult.get("allGrades");
                    for (Object key : allGrades.keySet()){
                        JSONObject taskJson = (JSONObject) allGrades.get(key);
                        int score = ((Long) taskJson.get("score")).intValue(); //SCORE
                        String keyString = (String) key;

                        int numberOfTask = extractTaskNumber(keyString);
                        Task matchingTask = newTest.getTasksOnTest().stream()
                                .filter(t -> t.getNumberOfTask() == numberOfTask)
                                .findFirst()
                                .orElse(null);

                        if (matchingTask != null) {
                            Grade grade = new Grade(score);
                            newStudentResult.addGrade(matchingTask, score);

                        }
                    }
                    newTest.addStudentResult(newStudentResult);
                }
                testItems.add(newTest);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        GradingSystemData.getInstance().setTestItems(testItems);
    }


    private int extractTaskNumber(String key) {
        Pattern pattern = Pattern.compile("Task number:(\\d+)");
        Matcher matcher = pattern.matcher(key);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid key format: " + key);
    }
}
