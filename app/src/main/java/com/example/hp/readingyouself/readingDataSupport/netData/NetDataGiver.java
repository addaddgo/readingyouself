package com.example.hp.readingyouself.readingDataSupport.netData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.hp.readingyouself.commentActivity.commentBean.BookCommentListBean;
import com.example.hp.readingyouself.commentActivity.commentBean.BookCommentReviewBean;
import com.example.hp.readingyouself.commentActivity.commentBean.BookRecommendBean;
import com.example.hp.readingyouself.commentActivity.commentBean.ComprehensiveAndOriginalCommentBean;
import com.example.hp.readingyouself.commentActivity.commentBean.ComprehensiveCommentBean;
import com.example.hp.readingyouself.commentActivity.commentBean.RecommendBookListBean;
import com.example.hp.readingyouself.readActivity.ReadingViewActivity;
import com.example.hp.readingyouself.readingDataSupport.dataForm.AllRankingBean;
import com.example.hp.readingyouself.readingDataSupport.dataForm.BookChapter;
import com.example.hp.readingyouself.readingDataSupport.dataForm.BookInCategoryListBean;
import com.example.hp.readingyouself.readingDataSupport.dataForm.BookInformation;
import com.example.hp.readingyouself.readingDataSupport.dataForm.BookInformationBean;
import com.example.hp.readingyouself.readingDataSupport.dataForm.BookSummary;
import com.example.hp.readingyouself.readingDataSupport.dataForm.CategoryBean;
import com.example.hp.readingyouself.readingDataSupport.dataForm.ChapterBodyBean;
import com.example.hp.readingyouself.readingDataSupport.dataForm.ChapterListBean;
import com.example.hp.readingyouself.readingDataSupport.dataForm.Rank;
import com.example.hp.readingyouself.readingDataSupport.dataForm.RankBean;
import com.example.hp.readingyouself.readingDataSupport.dataForm.SearchBookBean;
import com.example.hp.readingyouself.readingDataSupport.dataForm.TheBookCommentListBean;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;

/*
类简要描述：
    获取追书神器上的相关信息，并给出和合适的格式
类具体描述：

*/
public class NetDataGiver {

    private OkHttpClient client;
    private Gson gson;
    private String TAG = "NetDataGicer";

    public static String FEMALE_HOTTEST_RANKING_WEEK_URL = "http://api.zhuishushenqi.com/ranking/54d43437d47d13ff21cad58b";
    public static String MALE_HOTTEST_RANKING_WEEK_URL = "http://api.zhuishushenqi.com/ranking/54d42d92321052167dfb75e3";
    public static String MALE_HOTTEST_RANKING_MONTH_URL = "http://api.zhuishushenqi.com/ranking/564d820bc319238a644fb408";
    public static String FEMALE_HOTTEST_RANKING_MONTH_URL = "http://api.zhuishushenqi.com/ranking/564d853484665f97662d0810";
    public static String FEMALE_HOTTEST_RANKING_TOTAL_URL = "http://api.zhuishushenqi.com/ranking/564d85b6dd2bd1ec660ea8e2";
    public static String MALE_HOTTEST_RANKING_TOTAL_URL = "http://api.zhuishushenqi.com/ranking/564d8494fe996c25652644d2";

    public NetDataGiver() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    private static String TOTAL_START = "\"total\":";
    private static String TOTAL_END = "},";
    private static String ID = "\"_id\":\"";
    private static String TITLE = "\"title\":\"";
    private static String AUTHOR = "\"author\":\"";
    private static String SHORT_INTRODUCTION = "\"shortIntro\":\"";
    private static String COVER = "\"cover\":\"";
    private static String DOUBLE_QUOTATION = "\"";

    //获取最新的追书最热榜；
    //@return Rank
    public Rank getLatestHottestRanking(String url){
        return getBooksBySearchAndRankingResponse(url,TOTAL_END);
    }

    public  static String LATEST_CATEGORY_MALE_URL = "http://api.zhuishushenqi.com/cats/lv2";
    private static String NAME_CATEGORY = "\"name\":\"";
    private static String FEMALE = "\"male\":";
    private static String MALE = "\"female\":";

    //获取最新分类
    //@return>
    public CategoryBean getLatestCategory() {
        Request request = new Request.Builder().get().url(LATEST_CATEGORY_MALE_URL).build();
        Call call = client.newCall(request);
        Response response;
        try {
            response = call.execute();
            StringBuilder string = new StringBuilder(response.body().string());
            CategoryBean categoryBean = gson.fromJson(string.toString(),CategoryBean.class);
            return categoryBean;
        }catch (IOException e){
            return null;
        }
    }


    private static String COMMENT_URL = "http://api.zhuishushenqi.com/post/review/by-book?";
    private static String NICK_NAME = "\"nickname\":\"";
    private static String COMMENT_CONTENT = "\"content\":\"";
    public static String COMMENT_SORT_DEFAULT = "updated";
    public static String COMMENT_SORT_CREATED = "created";
    public static String COMMENT_SORT_HELPFUL = "helpful";
    public static String COMMENT_SORT_COMMENT_COUNT = "comment-count";
    //获取书评列表
    //@parameter : 书对应的ID  第一条评论对应整个评论的位置 返回的评论数目
    public TheBookCommentListBean  getCommentByBook(String id,int start,int limit){
        String url = COMMENT_URL + "book=" + id + "&sort=" + COMMENT_SORT_DEFAULT + "&start=" + start +"&limit=" + limit;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String returnString = response.body().string();
             return gson.fromJson(returnString,TheBookCommentListBean.class);
        }catch (IOException e){
            return null;
        }
    }

    private static String BOOK_COMMENT_REVIEW_BASIC_URL = "http://api.zhuishushenqi.com/post/review/";
    public BookCommentReviewBean getBookCommentReviewBean(String reviewId){
        String url = BOOK_COMMENT_REVIEW_BASIC_URL + reviewId;
        Response response = getResponseByURL(url,"bookCommentReviewBean");
        try{
            String data = response.body().string();
            return gson.fromJson(data,BookCommentReviewBean.class);
        }catch (IOException e){
            e.printStackTrace();
            Log.d(TAG, "getBookCommentReviewBean");
            return null;
        }
    }

    private static String BOOK_INFORMATION_URL = "http://api.zhuishushenqi.com/book/";
    private static String LONG_INTRODUCTION = "\"longIntro\":\"";
    private static String MAJOR_CATE = "\"majorCate\":\"";
    private static String MINOR_CATE = "\"minorCate\":\"";
    private static String LATELY_FOLLOWER = "\"latelyFollower\":";
    private static String WORD_COUNT = "\"wordCount\":";
    private static String CHAPTERS_COUNT = "\"chaptersCount\":";
    private static String LAST_CHAPTER = "\"lastChapter\":\"";
    private static String COPYRIGIHT = "\"copyright\":\"";
    private static String COMMA = ",";
    //获取书籍详细信息
    //@parameter 书ID
    //@return BookInformation
    public BookInformation getBookInformation(String id) {
        String url = BOOK_INFORMATION_URL + id;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String returnString = response.body().string();
            BookInformation bookInformation = new BookInformation();
            bookInformation.setID(id);
            int startIndex;
            int endIndex = 0;
            startIndex = returnString.indexOf(LONG_INTRODUCTION, endIndex) + LONG_INTRODUCTION.length();
            endIndex = returnString.indexOf(DOUBLE_QUOTATION, startIndex);
            bookInformation.setLongIntroduction(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(AUTHOR, endIndex) + AUTHOR.length();
            endIndex = returnString.indexOf(DOUBLE_QUOTATION, startIndex);
            bookInformation.setAuthor(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(MAJOR_CATE, endIndex) + MAJOR_CATE.length();
            endIndex = returnString.indexOf(DOUBLE_QUOTATION, startIndex);
            bookInformation.setMajorCate(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(MINOR_CATE, endIndex) + MAJOR_CATE.length();
            endIndex = returnString.indexOf(DOUBLE_QUOTATION, startIndex);
            bookInformation.setMinorCate(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(TITLE) + TITLE.length() + TITLE.length();
            endIndex = returnString.indexOf(DOUBLE_QUOTATION, startIndex);
            bookInformation.setTitle(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(LATELY_FOLLOWER, endIndex) + LATELY_FOLLOWER.length();
            endIndex = returnString.indexOf(COMMA, startIndex);
            bookInformation.setLatelyFollower(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(WORD_COUNT, endIndex) + WORD_COUNT.length();
            endIndex = returnString.indexOf(COMMA, startIndex);
            bookInformation.setWordCount(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(CHAPTERS_COUNT, endIndex) + CHAPTERS_COUNT.length();
            endIndex = returnString.indexOf(COMMA, startIndex);
            bookInformation.setChaptersCount(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(LAST_CHAPTER, endIndex) + LAST_CHAPTER.length();
            endIndex = returnString.indexOf(DOUBLE_QUOTATION, startIndex);
            bookInformation.setLastChapter(returnString.substring(startIndex, endIndex));
            startIndex = returnString.indexOf(COPYRIGIHT, endIndex) + COPYRIGIHT.length();
            endIndex = returnString.indexOf(DOUBLE_QUOTATION, startIndex);
            bookInformation.setCopyright(returnString.substring(startIndex, endIndex));
            return bookInformation;
        } catch (IOException e) {
            return null;
        }
    }


    public BookInformationBean getBookInformationBean(String id){
        String url = BOOK_INFORMATION_URL + id;
        Response response = getResponseByURL(url,"bookInformationBean");
        try{
            String data = response.body().string();
            return gson.fromJson(data,BookInformationBean.class);
        }catch (IOException e){
            e.printStackTrace();
            Log.d(TAG, "response.body");
            return null;
        }
    }

    //根据书名搜索
    private final static String SEARCH_URL = "http://novel.juhe.im/search?keyword=";
    public Rank search(String bookName){
        return getBooksBySearchAndRankingResponse(SEARCH_URL + bookName,COMMA);
    }

    //解析搜索和排行
    //根据书名搜索
    public Rank getBooksBySearchAndRankingResponse(String url, String separator){
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        Response response;
        try {
            response = call.execute();
            StringBuilder string = new StringBuilder(response.body().string());
            int locationStarOfTotalBooks = string.lastIndexOf(TOTAL_START);
            int locationEndOfTotalBooks = string.lastIndexOf(separator);
            int numberBooks = Integer.parseInt(string.subSequence(locationStarOfTotalBooks + TOTAL_START.length(), locationEndOfTotalBooks).toString());
            BookSummary[] bookSummaries = new BookSummary[numberBooks];
            int lastIndex = string.indexOf("books");
            for (int i = 0; i < bookSummaries.length; i++) {
                int idStart = string.indexOf(ID, lastIndex);
                int titleStart = string.indexOf(TITLE, lastIndex);
                int authorStart = string.indexOf(AUTHOR, lastIndex);
                int shortIntroductionStart = string.indexOf(SHORT_INTRODUCTION, lastIndex);
                int idEnd = string.indexOf(DOUBLE_QUOTATION, idStart + ID.length());
                int titleEnd = string.indexOf(DOUBLE_QUOTATION, titleStart + TITLE.length());
                int authorEnd = string.indexOf(DOUBLE_QUOTATION, authorStart + AUTHOR.length());
                int shortIntroductionEnd = string.indexOf(DOUBLE_QUOTATION, shortIntroductionStart + SHORT_INTRODUCTION.length());
                String id = string.substring(idStart + ID.length(), idEnd);
                String title = string.substring(titleStart + TITLE.length(), titleEnd);
                String author = string.substring(authorStart + AUTHOR.length(), authorEnd);
                String shortIntro = string.substring(shortIntroductionStart + SHORT_INTRODUCTION.length(), shortIntroductionEnd);
                bookSummaries[i] = new BookSummary(id, title, author, shortIntro);
                lastIndex = shortIntroductionEnd;
            }
            Rank rank = new Rank(url,bookSummaries);
            return rank;
        } catch (IOException e) {
            return null;
        }
    }

    //搜索书
    private final static String SEARCH_BASIC_URL = "http://novel.juhe.im/search?keyword=";
    public SearchBookBean getSearchBookBean(String book){
        String url = SEARCH_BASIC_URL + book;
        Response response = getResponseByURL(url,"searchbook");
        if(response != null){
            try {
                String data = response.body().string();
                return gson.fromJson(data,SearchBookBean.class);
            }catch (IOException e){
                e.printStackTrace();
                Log.d(TAG, "getSearchBookBean");
            }
        }
        return null;
    }

    private static String BOOK_LIST_URL_START = "http://api.zhuishushenqi.com/mix-atoc/";
    private static String BOOK_LIST_URL_END = "?view=chapters ";
    private static String CHAPTERS_COUNT_1 = "\"chaptersCount1\":";
    private static String BOOK_ID = "bookId=";
    private static String CHAPTER_LINK_CORRECT = "http://chapter2.zhuishushenqi.com/chapter/http:%2F%2Fbook.my716.com%2FgetBooks.aspx%3Fmethod=content&bookId=";
    private static String CHAPTER_LINK_MISTAKE = "http://book.my716.com/getBooks.aspx?method=content&bookId=";

    //获取书籍章节列表
    //@parameter书籍id
    public ArrayList<BookChapter> getChapterBooks(String id){
        Request request = new Request.Builder().get().url(BOOK_LIST_URL_START + id + BOOK_LIST_URL_END).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String returnString = response.body().string();
            int start = returnString.indexOf(TITLE, 0) + TITLE.length();
            int end = returnString.indexOf(DOUBLE_QUOTATION, start);
//            int start = returnString.indexOf(CHAPTERS_COUNT_1) + CHAPTERS_COUNT_1.length();
//            int end = returnString.indexOf(COMMA,start);
//            int numberTotal = Integer.parseInt(returnString.substring(start,end));
            ArrayList<BookChapter> bookChapters = new ArrayList<>();
            while (start != -1 + TITLE.length() && end != -1 + TITLE.length()) {
                String title = returnString.substring(start, end);
                start = returnString.indexOf(BOOK_ID, end) + BOOK_ID.length();
                end = returnString.indexOf(DOUBLE_QUOTATION, start);
                String link = CHAPTER_LINK_CORRECT + returnString.substring(start, end);
                BookChapter bookChapter = new BookChapter(link, title,id);
                bookChapters.add(bookChapter);
                start = returnString.indexOf(TITLE, start) + TITLE.length();
                end = returnString.indexOf(DOUBLE_QUOTATION, start);
            }
            return bookChapters;
        }catch (IOException e){
            return null;
        }
    }

    //获取章节列表
    public ChapterListBean getChapterListBean(String id){
        Request request = new Request.Builder().get().url(BOOK_LIST_URL_START + id + BOOK_LIST_URL_END).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String returnString = response.body().string();
            ChapterListBean chapterListBean =  gson.fromJson(returnString,ChapterListBean.class);
            if(chapterListBean != null){
                for (ChapterListBean.MixTocBean.ChaptersBean chaptersBean:chapterListBean.getMixToc().getChapters()) {
                    chaptersBean.setLink(chaptersBean.getLink().replace(CHAPTER_LINK_MISTAKE,CHAPTER_LINK_CORRECT));
                }
            }
            return chapterListBean;
        }catch (IOException e){
            return null;
        }
    }

    //获取章节内容
    //@parameter 章节url
    private static String BODY_START = "\"body\":\"";
    private static String BODY_END = "\"}}";
    public String getBodyByChapterUrl(String url){
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String returnString = response.body().string();
            return returnString.substring(returnString.indexOf(BODY_START) + BODY_START.length(),returnString.lastIndexOf(BODY_END));
        }catch (IOException e){
            return null;
        }
    }

    //获取章节内容
    public ChapterBodyBean getChapterBodyBean(String url){
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String returnString = response.body().string();
            return gson.fromJson(returnString,ChapterBodyBean.class);
        }catch (IOException e){
            return null;
        }
    }


    //返回由分类获得的书列表
    private final static String CATEGORY_GET_LIST_URL = "http://api.zhuishushenqi.com/book/by-categories?";
    public final static String CATEGORY_TYPE_HOT = "hot";
    public final static String CATEGORY_TYPE_NEW = "new";
    public final static String CATEGORY_TYPE_REPUTATION = "reputation";
    public final static String CATEGORY_TYPE_OVER = "over";
    public final static String CATEGORY_TYPE_MALE = "female";
    public final static String CATEGORY_TYPE_FEMALE = "male";


    public BookInCategoryListBean getBookInCategoryBean(String type, String major, String minor, int start, int limit){
        String url = CATEGORY_GET_LIST_URL + "type=" + type + "&major=" + major + "&minor=" + minor + "&start=" + start + "&limit=" + limit;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        try{
            Response response = call.execute();
            BookInCategoryListBean bookInCategoryBean = gson.fromJson(response.body().string(),BookInCategoryListBean.class);
            return bookInCategoryBean;
        }catch (IOException e){
            Log.d(TAG,"bookInCategoryBean");
            return null;
        }
    }


    private final static String COMPREHENSIVE_COMMENT_BASE_URL = "http://api.zhuishushenqi.com/post/by-block?";
    public final static String COMPREHENSIVE_COMMENT_BLOCK_RAMBLE = "ramble";
    public final static String ORIGINAL_COMMENT_ = "original";


    //综合区
    public ComprehensiveAndOriginalCommentBean getComprehensiveCommentList(String sort, int start, int limit, boolean distillate){
        String url = COMPREHENSIVE_COMMENT_BASE_URL + "block=" + COMPREHENSIVE_COMMENT_BLOCK_RAMBLE + "&duration=all&sort=" + sort
                + "&type=all&start=" + start + "&limit=" + limit + "&distillate+" + distillate;
        return getComprehensiveOrOriginalCommentBean(url);
    }

    //综合区或者原创区的评论详情
    private static final String COMPREHENSIVE_COMMENT_POST_BASIC_URL = "http://api.zhuishushenqi.com/post/";
    public ComprehensiveCommentBean getComprehensiveCommentBean(String postId){
        String url = COMPREHENSIVE_COMMENT_POST_BASIC_URL + postId;
        Response response = getResponseByURL(url,"ComprehensiveCommentBean");
        try{
            if(response != null){
                String data = response.body().string();
                ComprehensiveCommentBean comprehensiveCommentBean = gson.fromJson(data,ComprehensiveCommentBean.class);
                return comprehensiveCommentBean;
            }
            return null;
        }catch (IOException e){
            e.printStackTrace();
            Log.i(TAG, "getComprehensiveCommentBean");
            return  null;
        }
    }

    //原创区
    public ComprehensiveAndOriginalCommentBean getOriginalCommentList(String sort, int start, int limit, boolean distillate){
        String url = COMPREHENSIVE_COMMENT_BASE_URL + "block=" + ORIGINAL_COMMENT_ + "&duration=all&sort=" + sort
                + "&type=all&start=" + start + "&limit=" + limit + "&distillate+" + distillate;
        return getComprehensiveOrOriginalCommentBean(url);
    }

    private ComprehensiveAndOriginalCommentBean getComprehensiveOrOriginalCommentBean(String url){
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        Response response;
        try{
            response = call.execute();
            String data = response.body().string();
            ComprehensiveAndOriginalCommentBean commentBean = gson.fromJson(data,ComprehensiveAndOriginalCommentBean.class);
            return commentBean;
        }catch (IOException e){
            e.printStackTrace();
            Log.d(TAG,"comprehensiveCommentBean");
            return null;
        }
    }


    private final  static  String BOOK_COMMENT_BASE_URL = "http://api.zhuishushenqi.com/post/review?";
    //书荒互助区
    public BookCommentListBean getBookCommentBean(String sort, String type, int start, int limit, boolean distillate){
        String url = BOOK_COMMENT_BASE_URL + "duration=all&sort=" + sort + "&type=" + type + "&start=" + start + "&limit=" + limit + "&distillate" + distillate;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        Response response;
        try{
            response = call.execute();
            String data = response.body().string();
            BookCommentListBean commentBean = gson.fromJson(data,BookCommentListBean.class);
            return commentBean;
        }catch (IOException e){
            e.printStackTrace();
            Log.d(TAG,"TheBookCommentListBean");
            return null;
        }
    }

    //获取书荒互助区的详细评论
    public BookRecommendBean getBookRecommendBean(String helpId){
        String url = "http://api.zhuishushenqi.com/post/help/" + helpId;
        Response response = getResponseByURL(url,"BookRecommendBean");
        if(response != null){
            try{
                String data = response.body().string();
                BookRecommendBean bookRecommendBean = gson.fromJson(data,BookRecommendBean.class);
                if(bookRecommendBean != null){
                    return bookRecommendBean;
                }else {
                    return null;
                }
            }catch (IOException e){
                e.printStackTrace();
                Log.d(TAG, "getBookRecommendBean");
            }
        }
        return  null;
    }

    public final static String RECOMMEND_BOOK_BASE_URL = "http://api.zhuishushenqi.com/post/help?duration=all";
    public RecommendBookListBean getRecommendBookListBean(String sort,int start,int limit,boolean distillate){
        String url = RECOMMEND_BOOK_BASE_URL + "&sort=" + sort + "&start=" + start + "&limit=" + limit + "&distillate=" + distillate;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        Response response;
        try{
            response = call.execute();
            String data = response.body().string();
            RecommendBookListBean recommendBookListBean = gson.fromJson(data,RecommendBookListBean.class);
            return recommendBookListBean;
        }catch (IOException e){
            Log.d(TAG,"recommendBookListBean");
            return null;
        }
    }


    //获取所有的排行榜
    private final static String ALL_RANKING_URL = "http://api.zhuishushenqi.com/ranking/gender";
    public AllRankingBean  getAllRankingList(){
        Request request = new Request.Builder().get().url(ALL_RANKING_URL).build();
        Call call = client.newCall(request);
        Response response;
        try{
            response = call.execute();
            String data = response.body().string();
            AllRankingBean allRankingBean = gson.fromJson(data,AllRankingBean.class);
            return allRankingBean;
        }catch (IOException e){
            Log.d(TAG,"allRankingBean");
            return null;
        }
    }

    //获得某一书榜的具体内容
    private final static String RANKING_LIST_BASE_URL = "http://api.zhuishushenqi.com/ranking/";
    public RankBean getTotalRankBeanByRankId(String id){
        String url = RANKING_LIST_BASE_URL + id;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        Response response;
        try{
            response = call.execute();
            String data = response.body().string();
            RankBean rankBean = gson.fromJson(data,RankBean.class);
            return rankBean;
        }catch (IOException e){
            Log.d(TAG,"rankBean");
            return null;
        }
    }

    //获取段子手的头像
    private final static String AVATAR_BASIC_URL = "http://statics.zhuishushenqi.com";
    public Bitmap getPortraitByAvatar(String authorAvatar){
        String url = AVATAR_BASIC_URL + authorAvatar;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        Response response;
        try{
            response = call.execute();
            byte[] data = response.body().bytes();
            return BitmapFactory.decodeByteArray(data,0,data.length);
        }catch (IOException e){
            e.printStackTrace();
            Log.d(TAG,"Portrait");
            return null;
        }
    }

    //活动书的封面
    private final static String BOOK_COVER_BASIC_URL = "http://statics.zhuishushenqi.com";
    public Bitmap getBookCoverByAgent(String agent){
        String url = BOOK_COVER_BASIC_URL + agent;
        Response response =  getResponseByURL(url,"BookCover");
        if(response != null){
            try{
                byte[] coverData = response.body().bytes();
                return BitmapFactory.decodeByteArray(coverData,0,coverData.length);
            }catch (IOException e){
                e.printStackTrace();
                Log.i(TAG,"cover,bitmap");
                return null;
            }
        }else{
            return null;
        }
    }

    private Response getResponseByURL(String url,String information){
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        Response response;
        try{
            response = call.execute();
            return response;
        }catch (IOException e){
            e.printStackTrace();
            Log.i(TAG,information);
            return null;
        }
    }



//    class Bean<T>{
//        private T getBean(String url,T bean,String log){
//            Request request = new Request.Builder().get().url(url).build();
//            Call call = client.newCall(request);
//            Response response;
//            try{
//                response = call.execute();
//                String data = response.body().string();
//                T t = gson.fromJson(data,T.class);
//                return t;
//            }catch (IOException e){
//                Log.d(TAG,log);
//                return null;
//            }
//        }
//    }
}
