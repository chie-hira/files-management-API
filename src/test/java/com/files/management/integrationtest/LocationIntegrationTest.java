package com.files.management.integrationtest;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.files.management.entity.Location;
import com.files.management.mapper.LocationMapper;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DBRider
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LocationIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  private LocationMapper locationMapper;

  @Test
  @DataSet(value = "datasets/insert_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 保存場所を登録できること() throws Exception {
    String locationName = "新しい場所";
    String shelfNumber = "新しい棚";

    // モック設定
    when(locationMapper.isNotLocationUnique(locationName, shelfNumber)).thenReturn(false);

    // リクエスト作成
    String requestBody = """
        {
          "location": "新しい場所",
          "shelfNumber": "新しい棚"
        }
        """;

    // 実行
    String response = mockMvc.perform(MockMvcRequestBuilders.post("/locations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    // レスポンスの検証
    JSONAssert.assertEquals("""
        {
          "message": "保存場所情報を登録しました",
          "id": 6,
          "location": "新しい場所",
          "shelfNumber": "新しい棚"
        }
        """, response, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("id", ((o1, o2) -> true))
    ));
  }

  @Test
  @DataSet(value = "datasets/insert_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 保存場所が空で保存場所を作成しようとしたとき例外が投げられること() throws Exception {
    String locationName = "";
    String shelfNumber = "棚番";

    // リクエスト作成
    String requestBody = """
        {
          "location": "",
          "shelfNumber": "棚番"
        }
        """;

    // 実行
    String response = mockMvc.perform(MockMvcRequestBuilders.post("/locations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    // レスポンスの検証
    JSONAssert.assertEquals("""
        {
          "status": "400",
          "message": "location: location is required",
          "timestamp": "2024-01-17T22:47:08.854416+09:00[Asia/Tokyo]",
          "error": "Bad Request",
          "path": "/locations"
        }
        """, response, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("timestamp", ((o1, o2) -> true))));
  }

  @Test
  @DataSet(value = "datasets/insert_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 棚番号が空で保存場所を作成しようとしたとき例外が投げられること() throws Exception {
    String locationName = "場所";
    String shelfNumber = "";

    // リクエスト作成
    String requestBody = """
        {
          "location": "場所",
          "shelfNumber": ""
        }
        """;

    // 実行
    String response = mockMvc.perform(MockMvcRequestBuilders.post("/locations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    // レスポンスの検証
    JSONAssert.assertEquals("""
        {
          "status": "400",
          "message": "shelfNumber: location is required",
          "timestamp": "2024-01-17T22:47:08.854416+09:00[Asia/Tokyo]",
          "error": "Bad Request",
          "path": "/locations"
        }
        """, response, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("timestamp", ((o1, o2) -> true))));
  }

  @Test
  @DataSet(value = "datasets/insert_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 重複する保存場所を登録しようとしたとき例外が投げられること() throws Exception {
    String locationName = "場所";
    String shelfNumber = "棚";

    // リクエスト作成
    String requestBody = """
        {
          "location": "場所",
          "shelfNumber": "棚"
        }
        """;

    // モック設定
    when(locationMapper.isNotLocationUnique(locationName, shelfNumber)).thenReturn(true);

    String response = mockMvc.perform(MockMvcRequestBuilders.post("/locations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isConflict())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    // レスポンス検証
    JSONAssert.assertEquals("""
        {
          "timestamp": "2024-01-17T22:39:14.555576+09:00[Asia/Tokyo]",
          "message": "Location with location:場所 and shelfNumber:棚 already exists",
          "status": "409",
          "path": "/locations",
          "error": "Conflict"
        }
        """, response, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("timestamp", ((o1, o2) -> true))));
  }

  @Test
  @DataSet(value = "datasets/update_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 保存場所を更新できること() throws Exception {
    int id = 1;
    String locationName = "新しい場所";
    String shelfNumber = "新しい棚";

    // モック設定
    when(locationMapper.findById(id)).thenReturn(
        Optional.of(new Location(id, "既存場所", "既存棚")));
    when(locationMapper.isNotLocationUnique(locationName, shelfNumber)).thenReturn(false);

    // 更新リクエスト作成
    String requestBody = """
        {
          "id": 1,
          "location": "新しい場所",
          "shelfNumber": "新しい棚"
        }
        """;

    // 実行
    String response = mockMvc.perform(MockMvcRequestBuilders.put("/locations/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    // レスポンス検証
    JSONAssert.assertEquals("""
        {
          "message": "保存場所情報を更新しました",
          "id": 1,
          "location": "新しい場所",
          "shelfNumber": "新しい棚"
        }
        """, response, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("id", ((o1, o2) -> true))
    ));
  }

  @Test
  @DataSet(value = "datasets/insert_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 保存場所が空で保存場所を更新しようとしたとき例外が投げられること() throws Exception {
    int id = 1;
    String locationName = "";
    String shelfNumber = "棚番";

    // リクエスト作成
    String requestBody = """
        {
          "id": 1,
          "location": "",
          "shelfNumber": "棚番"
        }
        """;

    // 実行
    String response = mockMvc.perform(MockMvcRequestBuilders.put("/locations/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    // レスポンスの検証
    JSONAssert.assertEquals("""
        {
          "status": "400",
          "message": "location: location is required",
          "timestamp": "2024-01-17T22:47:08.854416+09:00[Asia/Tokyo]",
          "error": "Bad Request",
          "path": "/locations/1"
        }
        """, response, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("timestamp", ((o1, o2) -> true))));
  }

  @Test
  @DataSet(value = "datasets/insert_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 棚番号が空で保存場所を更新しようとしたとき例外が投げられること() throws Exception {
    int id = 1;
    String locationName = "場所";
    String shelfNumber = "";

    // リクエスト作成
    String requestBody = """
        {
          "id": 1,
          "location": "場所",
          "shelfNumber": ""
        }
        """;

    // 実行
    String response = mockMvc.perform(MockMvcRequestBuilders.put("/locations/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    // レスポンスの検証
    JSONAssert.assertEquals("""
        {
          "status": "400",
          "message": "shelfNumber: location is required",
          "timestamp": "2024-01-17T22:47:08.854416+09:00[Asia/Tokyo]",
          "error": "Bad Request",
          "path": "/locations/1"
        }
        """, response, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("timestamp", ((o1, o2) -> true))));
  }

  @Test
  @DataSet(value = "datasets/update_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 重複する保存場所に更新しようとしたとき例外が投げられること() throws Exception {
    int id = 1;
    String locationName = "場所";
    String shelfNumber = "棚";

    // リクエスト作成
    String requestBody = """
        {
          "id": 1,
          "location": "場所",
          "shelfNumber": "棚"
        }
        """;

    // モック設定
    when(locationMapper.findById(id)).thenReturn(
        Optional.of(new Location(id, "既存場所", "既存棚")));
    when(locationMapper.isNotLocationUnique(locationName, shelfNumber)).thenReturn(true);

    String response = mockMvc.perform(MockMvcRequestBuilders.put("/locations/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isConflict())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    // レスポンス検証
    JSONAssert.assertEquals("""
        {
          "timestamp": "2024-01-17T22:39:14.555576+09:00[Asia/Tokyo]",
          "message": "Location with location:場所 and shelfNumber:棚 already exists",
          "status": "409",
          "path": "/locations/1",
          "error": "Conflict"
        }
        """, response, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("timestamp", ((o1, o2) -> true))));
  }

  @Test
  @DataSet(value = "datasets/delete_locations.yml, datasets/insert_files.yml")
  @Transactional
  void 保存場所を削除できること() throws Exception {
    int id = 1;

    // モック設定
    when(locationMapper.findById(id)).thenReturn(
        Optional.of(new Location(id, "既存場所", "既存棚")));

    // 実行
    mockMvc.perform(MockMvcRequestBuilders.delete("/locations/{id}", id))
        .andExpect(status().isNoContent()); // HTTPステータスコードのみを検証する

    // 検証
    verify(locationMapper, times(1)).delete(id);
  }
}
