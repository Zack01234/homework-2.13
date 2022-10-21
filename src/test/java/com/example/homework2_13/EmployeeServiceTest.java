package com.example.homework2_13;

import com.example.homework2_13.exception.EmployeeAlreadyAddedException;
import com.example.homework2_13.exception.EmployeeNotFoundException;
import com.example.homework2_13.model.Employee;
import com.example.homework2_13.service.EmployeeService;
import com.example.homework2_13.service.ValidatorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class EmployeeServiceTest {
    private final EmployeeService employeeService = new EmployeeService(new ValidatorService());

    @AfterEach
    public void afterEach() {
        employeeService.getAll().forEach(employee -> employeeService.remove(employee.getName(), employee.getSurname()));
    }

    @Test
    public void addPositiveTest() {
        addOneWithCheck();
    }

    private Employee addOneWithCheck(String name, String surname) {
        Employee expected = new Employee(name, surname, 10_000, 1);
        int sizeBefore = employeeService.getAll().size();
        employeeService.add(expected.getName(), expected.getSurname(), expected.getSalary(), expected.getDepartment());
        assertThat(employeeService.getAll())
                .isNotEmpty()
                .hasSize(sizeBefore + 1)
                .contains(expected);
        assertThat(employeeService.find(expected.getName(), expected.getSurname())).isEquslTo(expected);
        return expected;
    }

    private Employee addOneWithCheck() {
        return addOneWithCheck("Name", "Surname");
    }

    @ParameterizedTest
    @MethodSource("addNegative1Params")
    public void addNegative1Test(String name, String surname, Class<Throwable> expectedExceptionType) {
        assertThatExceptionOfType(expectedExceptionType)
                .isThrownBy(() -> employeeService.add(name, surname, 10_000, 1));
    }

    @Test
    public void addNegative2Test() {
        Employee employee = addOneWithCheck();
        assertThatExceptionOfType(EmployeeAlreadyAddedException.class)
                .isThrownBy(() -> employeeService.add(employee.getName(), employee.getSurname(), employee.getSalary(), employee.getDepartment()));
    }

    @Test
    public void addNegative3Test() {
        for (int i = 0; i < 10; i++) {
            addOneWithCheck("Name" + (char) ('a' + i), "Surname" + (char) ('a' + i));
        }
        assertThatExceptionOfType(EmployeeAlreadyAddedException.class)
                .isThrownBy(() -> employeeService.add("Name", "Surname", 10, 1));
    }

    @Test
    public void findPositive() {
        Employee employee1 = addOneWithCheck("Name1", "Surname1");
        Employee employee2 = addOneWithCheck("Name2", "Surname2");
        assertThat(employeeService.find(employee1.getName(), employee1.getSurname()))
                .isEqualTo(employee1);
        assertThat(employeeService.find(employee2.getName(), employee2.getSurname()))
                .isEqualTo(employee2);
    }

    @Test
    public void findNegative() {
        assertThatExceptionOfType(EmployeeNotFoundException.class)
                .isThrownBy(() -> employeeService.find("test", "testov"));
        addOneWithCheck("Name1", "Surname1");
        addOneWithCheck("Name2", "Surname2");
        assertThatExceptionOfType(EmployeeNotFoundException.class)
                .isThrownBy(() -> employeeService.find("test", "testov"));
    }

    @Test
    public void removePositive() {
        Employee employee1 = addOneWithCheck("Name1", "Surname1");
        Employee employee2 = addOneWithCheck("Name2", "Surname2");
        employeeService.remove(employee1.getName(), employee1.getSurname());
        assertThat(employeeService.getAll())
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(employee1);
        employeeService.remove(employee2.getName(), employee2.getSurname());
        assertThat(employeeService.getAll()).isEmpty();
    }

    @Test
    public void removeNegative() {
        assertThatExceptionOfType(EmployeeNotFoundException.class)
                .isThrownBy(() -> employeeService.remove("test", "testov"));
        addOneWithCheck("Name1", "Surname1");
        addOneWithCheck("Name2", "Surname2");
        assertThatExceptionOfType(EmployeeNotFoundException.class)
                .isThrownBy(() -> employeeService.remove("test", "testov"));
    }

    public static Stream<Arguments> addNegative1Params() {
        return Stream.of(
                Arguments.of("Ivan1", "Petrov", IncorrectNameException.class),
                Arguments.of("Ivan%", "Petrov", IncorrectNameException.class),
                Arguments.of("Ivan", "Petrov1", IncorrectSurameException.class),
                Arguments.of("Ivan", "Petrov%", IncorrectSurameException.class)
        );
    }
}
