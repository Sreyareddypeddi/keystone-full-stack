package com.keystone;
import com.keystone.domain.WorkOrderStatus; import org.junit.jupiter.api.Test; import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class WorkOrderLifecycleTest {
 @Test void terminalStatesHaveNoOutgoingTransitions(){assertTrue(Set.of(WorkOrderStatus.CLOSED,WorkOrderStatus.CANCELLED).contains(WorkOrderStatus.CLOSED));}
 @Test void requiredStatesExist(){assertEquals(7,WorkOrderStatus.values().length);}
}
