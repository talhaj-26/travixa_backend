package com.travixa.repository.hotel;

import com.travixa.entity.hotel_stays.RoomRate;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.parser.PartTree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoomRateRepositoryQueryTests {

    @Test
    void applicableRateQueryPrioritizesLatestSeasonStartThenLowestSameSeasonRate() {
        PartTree query = new PartTree(
                "findFirstByRoomType_IdAndActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual"
                        + "OrderByValidFromDescNightlyRateAsc",
                RoomRate.class);

        assertEquals(4, query.getParts().get().count());
        Sort.Order seasonalPriority = query.getSort().getOrderFor("validFrom");
        Sort.Order tieBreak = query.getSort().getOrderFor("nightlyRate");
        assertNotNull(seasonalPriority);
        assertNotNull(tieBreak);
        assertEquals(Sort.Direction.DESC, seasonalPriority.getDirection());
        assertEquals(Sort.Direction.ASC, tieBreak.getDirection());
    }
}
