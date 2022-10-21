package com.example.homework2_13;

import com.example.homework2_13.exception.EmployeeNotFoundException;
import com.example.homework2_13.model.Employee;
import com.example.homework2_13.service.DepartmentService;
import com.example.homework2_13.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {
    @Mock
    private EmployeeService employeeService;
    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    public void beforeEach() {
        when(employeeService.getAll()).thenReturn(
                List.of(
                        new Employee("Ivan", "Ivanov", 10_000, 2),
                        new Employee("Oleg", "Olegov", 11_000, 2),
                        new Employee("Andrey", "Barov", 12_000, 3),
                        new Employee("Nikita", "Nikitov", 13_000, 3)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("employeeWithMaxSalaryTestParams")
    public void employeeWithMaxSalaryTest(int department, Employee expected) {
        assertThat(departmentService.employeeWithMaxSalary(department)).isEqualTo(expected);
    }

    @Test
    public void employeeWithMaxSalaryNegativeTest() {
        assertThatExceptionOfType(EmployeeNotFoundException.class)
                .isThrownBy(() -> departmentService.employeeWithMaxSalary(1));
    }

    @ParameterizedTest
    @MethodSource("employeeWithMinSalaryTestParams")
    public void employeeWithMinSalaryTest(int department, Employee expected) {
        assertThat(departmentService.employeeWithMinSalary(department)).isEqualTo(expected);
    }

    @Test
    public void employeeWithMinSalaryNegativeTest() {
        assertThatExceptionOfType(EmployeeNotFoundException.class)
                .isThrownBy(() -> departmentService.employeeWithMinSalary(1));
    }

    @ParameterizedTest
    @MethodSource("employeeFromDepartmentTestParams")
    public void employeeFromDepartmentTest(int department, Collection<Employee> expected) {
        assertThat(departmentService.employeesFromDepartment(department)).containsExactlyInAnyOrderElementsOf(expected);
    }
    @Test
    public void employeeFromDepartmentTest(Collection<Employee> expected) {
        assertThat(departmentService.employeesGroupedByDepartment(department)).containsExactlyInAnyOrderElementsOf(
                Map.of(
                        2, List.of(
                                new Employee("Ivan", "Ivanov", 10_000, 2),
                                new Employee("Oleg", "Olegov", 11_000, 2)
                        ),
                        3, List.of(
                                new Employee("Andrey", "Barov", 12_000, 3),
                                new Employee("Nikita", "Nikitov", 13_000, 3)
                        )
                )
        );
    }
    public static Stream<Arguments> employeeWithMaxSalaryTestParams() {
        return Stream.of(
                Arguments.of(2, new Employee("Oleg", "Olegov", 11_000, 2))
                Arguments.of(3, new Employee("Nikita", "Nikitov", 13_000, 3))
        );
    }

    public static Stream<Arguments> employeeWithMinSalaryTestParams() {
        return Stream.of(
                Arguments.of(2, new Employee("Ivan", "Ivanov", 10_000, 2))
                Arguments.of(3, new Employee("Andrey", "Barov", 12_000, 3))
        );
    }

    public static Stream<Arguments> employeeFromDepartmentTestParams() {
        return Stream.of(
                Arguments.of(1, Collection.emptyList()),
                Arguments.of(2,
                        List.of(
                                new Employee("Ivan", "Ivanov", 10_000, 2),
                                new Employee("Oleg", "Olegov", 11_000, 2)
                        )
                ),
                Arguments.of(3,
                        List.of(
                                new Employee("Andrey", "Barov", 12_000, 3),
                                new Employee("Nikita", "Nikitov", 13_000, 3)
                        )
                );
    }
}
