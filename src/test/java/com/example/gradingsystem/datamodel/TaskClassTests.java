package com.example.gradingsystem.datamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskClassTests {
    private Task task;

    @BeforeEach
    void setup(){
        task = new Task(1,100, TaskType.LISTENING);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(1, task.getNumberOfTask());
        assertEquals(100, task.getMaxPoints());
        assertEquals(TaskType.LISTENING, task.getType());
    }
    @Test
    void testSetters() {
        task.setNumberOfTask(2);
        assertEquals(2, task.getNumberOfTask());

        task.setMaxPoints(50);
        assertEquals(50, task.getMaxPoints());

        task.setType(TaskType.READING);
        assertEquals(TaskType.READING, task.getType());
    }

    @Test
    void testToString() {
        String expected = "Task number:1, maxPoints=100, type=LISTENING";
        assertEquals(expected, task.toString());
    }

    @Test
    void testSetMaxPointsWithNegativeValue() {
        assertThrows(IllegalArgumentException.class,
                ()->{task.setMaxPoints(-10);
        });
    }

    @Test
    void testSetNumberOfTaskWithNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            task.setNumberOfTask(-5);
        });
    }

    @Test
    void testSetTypeWithNull() {
        assertThrows(NullPointerException.class, () -> {
            task.setType(null);
        });
    }
}
