package ltd.newbee.mall.recommend.core;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import ltd.newbee.mall.recommend.dto.RelateDTO;

import java.util.*;
import java.util.stream.Collectors;

public class ItemCF {

    public static List<Long> recommend(Long userId, Integer num, List<RelateDTO> list) {
        // 按物品分组
        Map<Long, List<RelateDTO>> userMap = list.stream().collect(Collectors.groupingBy(RelateDTO::getUserId));
        List<Long> userProductItems = userMap.get(userId).stream().map(RelateDTO::getProductId).toList();
        Map<Long, List<RelateDTO>> itemMap = list.stream().collect(Collectors.groupingBy(RelateDTO::getProductId));
        List<Long> similarProductIdList = new ArrayList<>();
        // Map<Double, Long> itemTotalDisMap = new HashMap<>();
        Multimap<Double, Long> itemTotalDisMap = TreeMultimap.create();
        for (Long itemId : userProductItems) {
            // 获取其他物品与当前物品的关系值
            Map<Double, Long> itemDisMap = CoreMath.computeNeighbor(itemId, itemMap, 1);
            itemDisMap.forEach(itemTotalDisMap::put);
        }

        List<Double> values = new ArrayList<>(itemTotalDisMap.keySet());
        values.sort(Collections.reverseOrder());
        List<Double> scoresList = values.stream().limit(num).toList();
        // 获取关系最近的用户
        for (Double aDouble : scoresList) {
            Collection<Long> longs = itemTotalDisMap.get(aDouble);
            for (Long productId : longs) {
                if (!userProductItems.contains(productId)) {
                    similarProductIdList.add(productId);
                }
            }
        }
        return similarProductIdList.stream().distinct().limit(num).toList();

    }
}
