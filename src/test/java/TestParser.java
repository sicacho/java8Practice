import com.company.googledrive.MovieDTO;
import com.company.googledrive.MovieParser;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Administrator on 8/13/2016.
 */

public class TestParser {

    @Test
    public void testParseMovie() {
        List<MovieDTO> movieDTOList = MovieParser.getMoviesFromLink("http://www.javlibrary.com/en/vl_maker.php?&mode=2&m=ayeq&page=57");
        Assert.assertTrue(movieDTOList.size() > 15);
        System.out.println(movieDTOList.get(0).name + "-" + movieDTOList.get(0).link);
    }

    @Test
    public void testParseDetail() {
        MovieDTO movieDTO = MovieParser.getMovieFromDetailLink("http://www.javlibrary.com/en/?v=javlidzzre");
        Assert.assertNotNull(movieDTO.name);
        Assert.assertNotNull(movieDTO.description);
        Assert.assertNotNull(movieDTO.actors);
        Assert.assertNotNull(movieDTO.types);
        System.out.println(movieDTO.types.get(0));
        System.out.println(movieDTO.actors.get(0));
    }
}
