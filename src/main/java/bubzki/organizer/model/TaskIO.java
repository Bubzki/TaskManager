package bubzki.organizer.model;

import com.google.gson.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskIO {

    /**
     * Method that writes tasks from the list in stream in binary format.
     *
     * @param taskList the task list that need to write to the <code>out</code> stream.
     * @param out the output binary stream
     */
    public static void write(AbstractTaskList taskList, OutputStream out) {
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(taskList);
            //taskList.writeExternal(oos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that reads tasks from the stream in binary format to <code>taskList</code>.
     *
     * @param taskList the task list that takes data from the <code>in</code> stream
     * @param in the input binary stream
     */
    public static void read(AbstractTaskList taskList, InputStream in) {
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            AbstractTaskList tempTaskList = (AbstractTaskList) ois.readObject();
            for (Task temp : tempTaskList) {
                taskList.add(temp);
            }
            //taskList.readExternal(ois);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that writes the task list in binary format to the file.
     *
     * @param taskList the task list that need to write to the <code>file</code>
     * @param file the file to write
     */
    public static void writeBinary(AbstractTaskList taskList, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            write(taskList, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that reads the task list in binary format from the file to <code>taskList</code>.
     *
     * @param taskList the task list that takes data from the <code>file</code>
     * @param file the file to read
     */
    public static void readBinary(AbstractTaskList taskList, File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            read(taskList, fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that writes the task list in JSON format to the stream.
     *
     * @param taskList the task list that need to write to the <code>out</code> stream
     * @param out the output character stream
     */
    public static void write(AbstractTaskList taskList, Writer out) {
        try (BufferedWriter bufW = new BufferedWriter(out)) {
            GsonBuilder gsonBuilder;
            if (LinkedTaskList.class.equals(taskList.getClass())) {
                gsonBuilder = gsonBuilderSerializerForLinkedTaskList();
            } else {
                gsonBuilder = gsonBuilderSerializerForArrayTaskList();
            }
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            bufW.write(gson.toJson(taskList, taskList.getClass()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that reads the task list in JSON format from the stream to <code>taskList</code>.
     *
     * @param taskList the task list that takes data from the <code>in</code> stream
     * @param in the input character stream
     */
    public static void read(AbstractTaskList taskList, Reader in) {
        try (BufferedReader bufR = new BufferedReader(in)) {
            GsonBuilder gsonBuilder;
            if (LinkedTaskList.class.equals(taskList.getClass())) {
                gsonBuilder = gsonBuilderDeserializerForLinkedTaskList();
            } else {
                gsonBuilder = gsonBuilderDeserializerForArrayTaskList();
            }
            Gson gson = gsonBuilder.create();
            for (Task temp : gson.fromJson(bufR, taskList.getClass())) {
                taskList.add(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that writes the task list in JSON format to the file.
     *
     * @param taskList the task list that need to write to the <code>file</code>
     * @param file the file to write
     */
    public static void writeText(AbstractTaskList taskList, File file) {
        try (FileWriter fw = new FileWriter(file)) {
            write(taskList, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that reads the task list in JSON format from the file to <code>taskList</code>.
     *
     * @param taskList the task list that takes data from the <code>file</code>
     * @param file the file to read
     */
    public static void readText(AbstractTaskList taskList, File file) {
        try (FileReader fr = new FileReader(file)) {
            read(taskList, fr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that creates {@link GsonBuilder} with correct parameters for serialization {@link LinkedTaskList}.
     *
     * @return the {@link GsonBuilder} with correct parameters
     */
    private static GsonBuilder gsonBuilderSerializerForLinkedTaskList() {
        GsonBuilder gsonBuilder = gsonBuilderSerializerForLocalDateTime();
        gsonBuilder.registerTypeAdapter(LinkedTaskList.class, (JsonSerializer<LinkedTaskList>) (linkedTaskList, type, jsonSerializationContext) -> {
            JsonObject jo = new JsonObject();
            JsonArray tasks = new JsonArray();
            for (Task temp : linkedTaskList) {
                tasks.add(jsonSerializationContext.serialize(temp));
            }
            jo.addProperty("size", linkedTaskList.size());
            jo.add("tasks", tasks);
            return jo;
        });
        return gsonBuilder;
    }

    /**
     * Method that creates {@link GsonBuilder} with correct parameters for deserialization {@link LinkedTaskList}.
     *
     * @return the {@link GsonBuilder} with correct parameters
     */
    private static GsonBuilder gsonBuilderDeserializerForLinkedTaskList() {
        GsonBuilder gsonBuilder = gsonBuilderDeserializerForLocalDateTime();
        gsonBuilder.registerTypeAdapter(LinkedTaskList.class, (JsonDeserializer<LinkedTaskList>) (json, type, jsonDeserializationContext) -> {
            LinkedTaskList linkedTaskList = new LinkedTaskList();
            int size = json.getAsJsonObject().get("size").getAsInt();
            for (int i = 0; i < size; ++i) {
                linkedTaskList.add(jsonDeserializationContext.deserialize(json.getAsJsonObject().get("tasks").getAsJsonArray().get(i).getAsJsonObject(), Task.class));
            }
            return linkedTaskList;
        });
        return gsonBuilder;
    }

    /**
     * Method that creates {@link GsonBuilder} with correct parameters for serialization {@link ArrayTaskList}.
     *
     * @return the {@link GsonBuilder} with correct parameters
     */
    private static GsonBuilder gsonBuilderSerializerForArrayTaskList() {
        GsonBuilder gsonBuilder = gsonBuilderSerializerForLocalDateTime();
        gsonBuilder.registerTypeAdapter(Task[].class, (JsonSerializer<Task[]>) (tasks, type, jsonSerializationContext) -> {
            JsonArray ja = new JsonArray();
            for (Task temp : tasks) {
                if (temp == null) {
                    break;
                }
                ja.add(jsonSerializationContext.serialize(temp));
            }
            return ja;
        });
        return gsonBuilder;
    }

    /**
     * Method that creates {@link GsonBuilder} with correct parameters for deserialization {@link ArrayTaskList}.
     *
     * @return the {@link GsonBuilder} with correct parameters
     */
    private static GsonBuilder gsonBuilderDeserializerForArrayTaskList() {
        return gsonBuilderDeserializerForLocalDateTime();
    }

    /**
     * Method that creates {@link GsonBuilder} with correct parameters for serialization {@link LocalDateTime}.
     *
     * @return the {@link GsonBuilder} with correct parameters
     */
    private static GsonBuilder gsonBuilderSerializerForLocalDateTime() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (ldt, type, jsonSerializationContext) ->
                //new JsonPrimitive(ldt.toEpochSecond(ZoneOffset.UTC)));
                new JsonPrimitive(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(ldt)));
        gsonBuilder.registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (duration, type, jsonSerializationContext) ->
                new JsonPrimitive(duration.getSeconds()));
        return gsonBuilder;
    }

    /**
     * Method that creates {@link GsonBuilder} with correct parameters for deserialization {@link LocalDateTime}.
     *
     * @return the {@link GsonBuilder} with correct parameters
     */
    private static GsonBuilder gsonBuilderDeserializerForLocalDateTime() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                //LocalDateTime.ofEpochSecond(json.getAsLong(), 0, ZoneOffset.UTC));
                LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        gsonBuilder.registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (json, type, jsonDeserializationContext) ->
                Duration.ofSeconds(json.getAsLong()));
        return gsonBuilder;
    }
}
