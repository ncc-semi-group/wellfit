-- auto-generated definition
create table badge
(
    id              int auto_increment comment '뱃지아이디'
        primary key,
    badge_name      varchar(100) null comment '뱃지명',
    badge_contents  varchar(255) null comment '뱃지내용',
    badge_images    varchar(255) null comment '뱃지이미지',
    condition_type  varchar(255) null comment '뱃지조건명',
    condition_value int          null comment '뱃지조건만족수'
);

-- auto-generated definition
create table board
(
    id         int auto_increment comment '게시판id'
        primary key,
    user_id    int                                 not null comment 'id',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '생성시간',
    content    text                                null comment '게시판 본문',
    constraint FK_user_to_board
        foreign key (user_id) references user (id)
);

-- auto-generated definition
create table board_hashtag
(
    board_id int not null comment '게시판id',
    tag_id   int not null comment '태그 id',
    primary key (board_id, tag_id),
    constraint FK_board_to_board_hashtag
        foreign key (board_id) references board (id),
    constraint FK_hashtag_to_board_hashtag
        foreign key (tag_id) references hashtag (id)
);

-- auto-generated definition
create table board_image
(
    id        int auto_increment comment '이미지id'
        primary key,
    board_id  int          not null comment '게시판id',
    file_name varchar(255) null comment '이미지이름',
    constraint FK_board_to_board_image
        foreign key (board_id) references board (id)
);

-- auto-generated definition
create table board_like
(
    id       int auto_increment comment '좋아요id'
        primary key,
    user_id  int not null comment '회원id',
    board_id int not null comment '게시판id',
    constraint FK_board_to_board_like
        foreign key (board_id) references board (id),
    constraint FK_user_to_board_like
        foreign key (user_id) references user (id)
);

-- auto-generated definition
create table chat
(
    id           int auto_increment comment '채팅아이디'
        primary key,
    user_id      int                                 not null comment 'id',
    chatroom_id  int                                 not null comment '채팅방 아이디',
    message      text                                null comment '내용',
    write_time   timestamp default CURRENT_TIMESTAMP null comment '작성시간',
    message_type varchar(10)                         null comment '메시지종류',
    constraint FK_chatroom_to_chat
        foreign key (chatroom_id) references chatroom (id),
    constraint FK_user_to_chat
        foreign key (user_id) references user (id)
);

-- auto-generated definition
create table chatroom
(
    id            int auto_increment comment '채팅방 아이디'
        primary key,
    room_name     varchar(255)  null comment '채팅방이름',
    reading_count int default 0 not null comment '채팅방 이용중인 유저 수'
);

-- auto-generated definition
create table chatroom_user
(
    id               int auto_increment comment '채팅방사용자다대다아이디'
        primary key,
    chatroom_id      int                                 not null comment '채팅방 아이디',
    user_id          int                                 not null comment '유저아이디',
    created_at       timestamp default CURRENT_TIMESTAMP not null comment '생성시간',
    lastly_read_time timestamp default CURRENT_TIMESTAMP null comment '마지막 읽은 시간',
    constraint FK_chatroom_to_chatroom_user
        foreign key (chatroom_id) references chatroom (id),
    constraint FK_user_to_chatroom_user
        foreign key (user_id) references user (id)
);

-- auto-generated definition
create table comment
(
    id        int auto_increment comment '댓글id'
        primary key,
    board_id  int       not null comment '게시판id',
    user_id   int       not null comment '회원id',
    comment   text      null comment '댓글내용',
    parent_id int       not null comment '부모 id',
    Field     timestamp null comment '댓글 작성시간',
    id22      int       not null comment '게시판id',
    constraint FK_board_to_comment
        foreign key (board_id) references board (id),
    constraint FK_comment_to_comment_board_id
        foreign key (id22) references comment (board_id),
    constraint FK_parent_comment_to_comment
        foreign key (parent_id) references comment (id),
    constraint FK_user_to_comment
        foreign key (user_id) references user (id)
);

-- auto-generated definition
create table daily_statistics
(
    id                  int auto_increment comment 'id'
        primary key,
    user_id             int                                                            null comment '유저id',
    date                date                                                           null comment '일자',
    total_kcal          int                                          default 0         null comment '칼로리',
    total_protein       float                                                          null comment '단백질',
    total_fat           float                                                          null comment '지방',
    total_carbohydrate  float                                                          null comment '탄수화물',
    total_sugar         float                                                          null comment '당류',
    total_natrium       int                                                            null comment '나트륨',
    total_cholesterol   float                                                          null comment '콜레스트롤',
    total_saturated_fat float                                                          null comment '포화지방',
    total_trans_fat     float                                                          null comment '트랜스지방',
    weight              float                                        default 0         null comment '체중',
    total_burned_kcal   int                                          default 0         null comment '운동량',
    total_exercise_time smallint                                                       null comment '운동 시간',
    recommend_kcal      smallint                                                       null comment '권장 칼로리',
    is_achieved         tinyint(1)                                   default 0         null comment '달성 여부',
    cheating_kcal       smallint                                     default 0         null comment '치팅 칼로리',
    goal_weight         float                                                          null comment '체중',
    target              enum ('default', 'maintain', 'diet', 'gain') default 'default' null comment '목표',
    constraint daily_statistics_user_id_fk
        foreign key (user_id) references user (id)
            on delete cascade
);

create index daily_statistics_user_id_date_index
    on daily_statistics (user_id, date);

-- auto-generated definition
create table exercise_record
(
    id             int auto_increment comment 'id'
        primary key,
    user_id        int                  not null comment 'user_id',
    exercise_type  varchar(255)         null comment '운동 종류',
    exercise_level enum ('1', '2', '3') null comment '운동강도(1/2/3)',
    burned_kcal    smallint             null comment '소모 칼로리',
    exercise_time  smallint             null comment '운동시간(분)',
    date           date                 null comment '일자',
    constraint FK_user_to_exercise_record
        foreign key (user_id) references user (id)
            on delete cascade
);

create index exercise_record_user_id_date_index
    on exercise_record (user_id, date);

-- auto-generated definition
create table follow
(
    id       int auto_increment comment '팔로우id'
        primary key,
    user_id1 int not null comment '팔로워id',
    user_id2 int not null comment '팔로잉id',
    constraint FK_user1_to_follow
        foreign key (user_id1) references user (id),
    constraint FK_user2_to_follow
        foreign key (user_id2) references user (id)
);

-- auto-generated definition
create table food_favorites
(
    id        int auto_increment comment 'id'
        primary key,
    user_id   int                     null comment '유저id',
    food_type enum ('system', 'user') null comment '음식 타입',
    food_id   int                     null comment '음식 id',
    constraint food_favorites_ibfk_1
        foreign key (user_id) references user (id)
);

create index user_id
    on food_favorites (user_id);

-- auto-generated definition
create table food_nutrition
(
    id                int auto_increment comment '음식id'
        primary key,
    name              varchar(50)            null comment '식품명',
    standard          enum ('100g', '100ml') null comment '기준량 (100g, 100ml)',
    kcal              smallint               null comment '에너지 (kcal)',
    protein           float                  null comment '단백질',
    fat               float                  null comment '지방',
    carbohydrate      float                  null comment '탄수화물',
    sugar             float                  null comment '당류',
    natrium           int                    null comment '나트륨',
    cholesterol       float                  null comment '콜레스트롤',
    saturated_fat     float                  null comment '포화지방산',
    trans_fat         float                  null comment '트랜스지방산',
    manufacturer_name varchar(50)            null comment '브랜드/제조사명',
    serving_size      smallint               null comment '1회 섭취참고량',
    weight            float                  null comment '식품중량'
);

create index food_nutrition_id_index
    on food_nutrition (id);

-- auto-generated definition
create table food_records
(
    id         int auto_increment comment 'id'
        primary key,
    user_id    int                                            not null comment '유저 id',
    meal_type  enum ('breakfast', 'lunch', 'dinner', 'snack') null comment '식사 타입',
    date       date                                           null comment '일자',
    created_at timestamp default CURRENT_TIMESTAMP            not null comment '생성시간',
    kcal       int       default 0                            null comment '칼로리',
    constraint UC_food_records_user_meal_date
        unique (user_id, meal_type, date),
    constraint FK_user_to_food_records
        foreign key (user_id) references user (id)
            on delete cascade
);

create index food_records_user_id_date_index
    on food_records (user_id, date);

-- auto-generated definition
create table food_templates
(
    id         int auto_increment comment 'id'
        primary key,
    user_id    int                                 null comment '유저 id',
    name       varchar(50)                         null comment '템플릿 이름',
    created_at timestamp default CURRENT_TIMESTAMP not null comment '생성시간',
    kcal       int       default 0                 null comment '칼로리',
    constraint food_templates_user_id_fk
        foreign key (user_id) references user (id)
            on delete cascade
);

create index food_templates_user_id_index
    on food_templates (user_id);

-- auto-generated definition
create table friendship
(
    id       int auto_increment comment '친구관리 id'
        primary key,
    user1_id int not null comment '유저1 id',
    user2_id int not null comment '유저2 id',
    constraint FK_user1_to_friendship
        foreign key (user1_id) references user (id),
    constraint FK_user2_to_friendship
        foreign key (user2_id) references user (id)
);

-- auto-generated definition
create table friendship_request
(
    id              int auto_increment comment '친구요청 id'
        primary key,
    request_message varchar(255) null comment '소개 메세지',
    user_id         int          not null comment '요청 받은 사용자 id',
    sender_id       int          not null comment '요청 보낸 사용자 id'
);

-- auto-generated definition
create table hashtag
(
    id  int auto_increment comment '해시태그 id'
        primary key,
    tag varchar(255) null comment '태그'
);

-- auto-generated definition
create table individual_food_nutrition
(
    id                int auto_increment comment '음식id'
        primary key,
    user_id           int                                 null comment '유저id',
    name              varchar(50)                         null comment '식품명',
    standard          enum ('100g', '100ml')              null comment '기준량',
    kcal              smallint                            null comment '에너지 (kcal)',
    protein           float                               null comment '단백질',
    fat               float                               null comment '지방',
    carbohydrate      float                               null comment '탄수화물',
    sugar             float                               null comment '당류',
    natrium           int                                 null comment '나트륨',
    cholesterol       float                               null comment '콜레스트롤',
    saturated_fat     float                               null comment '포화지방산',
    trans_fat         float                               null comment '트랜스지방산',
    manufacturer_name varchar(255)                        null comment '브랜드/제조사명',
    serving_size      int                                 null comment '1회 섭취참고량',
    weight            float                               null comment '식품중량',
    created_at        timestamp default CURRENT_TIMESTAMP not null comment '생성시간',
    constraint individual_food_nutrition_user_id_fk
        foreign key (user_id) references user (id)
            on delete cascade
);

create index individual_food_nutrition_user_id_index
    on individual_food_nutrition (user_id);

-- auto-generated definition
create table record_items
(
    id              int auto_increment comment 'id'
        primary key,
    food_records_id int                     not null comment '식단기록 id',
    food_id         int                     null comment '음식 id',
    food_type       enum ('system', 'user') null comment '음식 타입',
    amount          smallint                null comment '양 (중량)',
    serving         smallint                null comment '인분',
    constraint FK_food_records_to_record_items
        foreign key (food_records_id) references food_records (id)
            on delete cascade
);

create index record_items_food_records_id_index
    on record_items (food_records_id);

-- auto-generated definition
create table template_items
(
    id               int auto_increment comment 'id'
        primary key,
    food_template_id int                     not null comment '템플릿 id',
    food_type        enum ('system', 'user') null comment '음식 타입',
    food_id          int                     null comment '음식 id',
    amount           int                     null comment '양 (중량)',
    serving          smallint                null comment '인분',
    constraint FK_food_templates_to_template_items
        foreign key (food_template_id) references food_templates (id)
            on delete cascade
);

create index template_items_food_template_id_index
    on template_items (food_template_id);

-- auto-generated definition
create table user
(
    id             int auto_increment comment 'id'
        primary key,
    email          varchar(255)                                                             null comment '이메일',
    password       char(64)                                                                 null comment '비밀번호 (SHA-256 Hex 해싱)',
    nickname       varchar(255)                                                             null comment '닉네임',
    profile_image  varchar(255)                                                             null comment '프로필 사진',
    gender         enum ('male', 'female')                                                  null comment '성별',
    age            int                                                                      null comment '나이',
    height         int                                                                      null comment '키',
    activity_level enum ('Sedentary', 'Lightly Active', 'Moderately Active', 'Very Active') null comment '활동계수',
    current_weight float                                                                    null comment '현재 체중',
    goal_weight    float                                                                    null comment '목표 체중',
    cheating_day   smallint     default 0                                                   null comment '치팅 데이',
    type           enum ('custom', 'common', 'health', 'quito', 'vegan')                    null comment '식단계획 유형',
    carbohydrate   smallint                                                                 null comment '탄수화물',
    protein        smallint                                                                 null comment '단백질',
    fat            smallint                                                                 null comment '지방',
    created_at     timestamp    default CURRENT_TIMESTAMP                                   not null comment '생성시간',
    my_intro       varchar(100) default '안녕하세요'                                             null
);

