DROP TABLE USERS CASCADE CONSTRAINTS;
DROP TABLE MEMBER CASCADE CONSTRAINTS;
DROP TABLE NOTICE CASCADE CONSTRAINTS;
DROP TABLE BOARD CASCADE CONSTRAINTS;
DROP TABLE REPLY CASCADE CONSTRAINTS;

-- �Ҽȷα��ν� ȸ�������� �̿��� ����� ȸ�� ���̺�
CREATE TABLE MEMBER(
  USERID VARCHAR2(50) CONSTRAINT PK_MEMBER_UID PRIMARY KEY,
  USERPWD VARCHAR2(100),
  USERNAME VARCHAR2(20) NOT NULL,
  GENDER CHAR(1)  NOT NULL,
  AGE NUMBER(3)   NOT NULL,
  PHONE VARCHAR2(13),
  EMAIL VARCHAR2(30) not null,  
  ENROLL_DATE DATE DEFAULT SYSDATE,
  LASTMODIFIED DATE DEFAULT SYSDATE,
  signtype VARCHAR2(10) default 'direct' not null,
  -- ���Թ�� : ���� ����('direct'), �Ҽȷα���('kakao', 'naver', 'google') 
  ADMIN_YN CHAR(1) DEFAULT 'N' not null,
  login_ok CHAR(1) DEFAULT 'Y' not null,
  photo_filename  varchar2(100)
  );

comment on column member.userid is 'ȸ�����̵�';
comment on column member.userpwd is 'ȸ���н�����';
comment on column member.username is 'ȸ���̸�';
comment on column member.gender is 'ȸ������';
comment on column member.age is 'ȸ������';
comment on column member.email is 'ȸ���̸���';
comment on column member.phone is 'ȸ����ȭ��ȣ';
comment on column member.enroll_date is 'ȸ�����Գ�¥';
comment on column member.userid is 'ȸ�����̵�';
comment on column member.lastmodified is '������������¥';
comment on column member.signtype is '���Թ��';
comment on column member.login_ok is '�α��ΰ��ɿ���';
comment on column member.admin_yn is '�����ڿ���';
comment on column member.photo_filename is 'ȸ���������ϸ�';

CREATE TABLE USERS (
  USERID VARCHAR2(15) PRIMARY KEY,
  USERPWD VARCHAR2(100) NOT NULL,
  USERNAME VARCHAR2(20) NOT NULL,
  ROLE	VARCHAR2(30) DEFAULT 'MEMBER' NOT NULL
);

ALTER TABLE USERS
DROP PRIMARY KEY;

-- �������� �߰��� �� �������� �߰���
ALTER TABLE USERS
ADD CONSTRAINT FK_MEMBER_UID 
FOREIGN KEY(USERID) REFERENCES MEMBER ON DELETE CASCADE;

-- TRIGGER �ۼ� : �̸� - TRI_INSERT_USERS
-- MEMBER ���̺� �� ȸ�������� ��ϵǸ�, �ڵ����� USERS ���̺� ���̵�, ��ȣ, �̸�, ROLL��
-- INSERT �ǰ� ��
CREATE OR REPLACE TRIGGER TRI_INSERT_USERS
AFTER INSERT ON MEMBER
FOR EACH ROW
BEGIN
  INSERT INTO USERS
  VALUES (:NEW.USERID, :NEW.USERPWD, :NEW.USERNAME, DEFAULT);  

  IF :NEW.ADMIN_YN = 'Y' THEN
  	UPDATE USERS
	SET ROLE = 'ADMIN'
	WHERE USERID = :NEW.USERID;
  END IF;  
  
END;
/

CREATE OR REPLACE TRIGGER TRI_UPDATE_USERS
AFTER UPDATE ON MEMBER
FOR EACH ROW
BEGIN
	IF :NEW.USERPWD != :OLD.USERPWD THEN
	  UPDATE USERS 
	  SET USERPWD = :NEW.USERPWD		  
	  WHERE USERID = :OLD.USERID;	
    END IF;
    
    IF :NEW.ADMIN_YN = 'Y' THEN
	  UPDATE USERS 
	  SET ROLE = 'ADMIN'		  
	  WHERE USERID = :OLD.USERID;	
    END IF;
    
    IF :NEW.ADMIN_YN = 'N' THEN
	  UPDATE USERS 
	  SET ROLE = 'MEMBER'		  
	  WHERE USERID = :OLD.USERID;	
    END IF;
	
END;
/

INSERT INTO MEMBER VALUES ('admin', 'admin', '������', 'M', 35, '010-1111-9999', 
'admin@ncs.kr', to_date('2016-06-25', 'RRRR-MM-DD'), sysdate, default, 'Y', default, null);

INSERT INTO MEMBER
VALUES ('user01', 'pass01', 'ȫ�浿', 'M', 25, '010-1234-5678', 'hong1234@ncs.kr', 
        default, default, default, default, default, null);
        
INSERT INTO MEMBER
VALUES ('user02', 'pass02', '�Ż��Ӵ�', 'F', 45, '010-4545-9999', 'dano99@ncs.kr', 
        default, default, 'direct', default, default, null);
        

COMMIT;

SELECT * FROM MEMBER;
SELECT * FROM USERS;

-- notice ���̺�
CREATE TABLE NOTICE(
  NOTICENO NUMBER CONSTRAINT PK_NOTICENO PRIMARY KEY,
  NOTICETITLE VARCHAR2(50) NOT NULL,
  NOTICEDATE DATE DEFAULT SYSDATE,
  NOTICEWRITER VARCHAR2(50) NOT NULL,
  NOTICECONTENT VARCHAR2(2000),
  ORIGINAL_FILEPATH VARCHAR2(100),
  RENAME_FILEPATH VARCHAR2(100),
  IMPORTANCE CHAR(1) DEFAULT 'N' NOT NULL,  
  IMP_END_DATE DATE DEFAULT SYSDATE NOT NULL,
  READCOUNT NUMBER DEFAULT 1 NOT NULL,
  CONSTRAINT FK_NOTICEWRITER FOREIGN KEY (NOTICEWRITER) 
      REFERENCES MEMBER ON DELETE CASCADE
);

COMMENT ON COLUMN notice.NOTICENO IS '�����۹�ȣ';
COMMENT ON COLUMN notice.NOTICETITLE IS '����������';
COMMENT ON COLUMN notice.NOTICEDATE IS '������ϳ�¥';
COMMENT ON COLUMN notice.NOTICEWRITER IS '�����ۼ���';
COMMENT ON COLUMN notice.NOTICECONTENT IS '��������';
COMMENT ON COLUMN notice.ORIGINAL_FILEPATH IS '����÷�����ϸ�';
COMMENT ON COLUMN notice.RENAME_FILEPATH IS '�ٲ�÷�����ϸ�';
COMMENT ON COLUMN notice.IMPORTANCE IS '�߿䵵';
COMMENT ON COLUMN notice.IMP_END_DATE IS '�߿䵵�Խ����ᳯ¥';
COMMENT ON COLUMN notice.READCOUNT IS '������ȸ��';

COMMIT;

INSERT INTO NOTICE VALUES (1, '���� ���� ����', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), 
'���� ���� ����2', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���� ���� ����3', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���� ���� ����4', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���� ���� ����5', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���� ���� ����6', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���� ���� ����7', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���� ���� ����8', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���� ���� ����9', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���� ���� ����10', SYSDATE, 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���Ի�� ��������', 
TO_DATE('2016-07-15', 'RRRR-MM-DD'), 'admin', 
'���� ���񽺰� ���µǾ����ϴ�. ���� �̿��� �ּ���.', null, null, DEFAULT, DEFAULT, DEFAULT);
INSERT INTO NOTICE VALUES ((select max(noticeno) + 1 from notice), '���Ի�� �������� ����', 
TO_DATE('2016-07-20', 'RRRR-MM-DD'), 'admin', 
'2016�� 7�� 20�� 18�ÿ� ���Ի�� ������ �����մϴ�.', null, null, DEFAULT, DEFAULT, DEFAULT);

SELECT * FROM NOTICE;
 
commit;



-- BOARD TABLE
CREATE TABLE BOARD(
	BOARD_NUM	NUMBER,	
	BOARD_WRITER	 VARCHAR2(50) NOT NULL,
	BOARD_TITLE	VARCHAR2(50) NOT NULL,
	BOARD_CONTENT	VARCHAR2(2000) NOT NULL,
	BOARD_ORIGINAL_FILENAME	VARCHAR2(100),
    BOARD_RENAME_FILENAME VARCHAR2(100),    
	BOARD_READCOUNT	NUMBER DEFAULT 0,
	BOARD_DATE	DATE DEFAULT SYSDATE,
  CONSTRAINT PK_BOARD PRIMARY KEY (BOARD_NUM),
  CONSTRAINT FK_BOARD_WRITER FOREIGN KEY (BOARD_WRITER) REFERENCES MEMBER (USERID) ON DELETE CASCADE
);

COMMENT ON COLUMN BOARD.BOARD_NUM IS '�Խñ۹�ȣ';
COMMENT ON COLUMN BOARD.BOARD_WRITER IS '�ۼ��ھ��̵�';
COMMENT ON COLUMN BOARD.BOARD_TITLE IS '�Խñ�����';
COMMENT ON COLUMN BOARD.BOARD_CONTENT IS '�Խñ۳���';
COMMENT ON COLUMN BOARD.BOARD_DATE IS '�ۼ���¥';
COMMENT ON COLUMN BOARD.BOARD_ORIGINAL_FILENAME IS '����÷�����ϸ�';
COMMENT ON COLUMN BOARD.BOARD_RENAME_FILENAME IS '�ٲ�÷�����ϸ�';
COMMENT ON COLUMN BOARD.BOARD_READCOUNT IS '������ȸ��';

CREATE TABLE REPLY (
	REPLY_NUM	NUMBER,	
	REPLY_WRITER	 VARCHAR2(50) NOT NULL,
	REPLY_TITLE	VARCHAR2(50) NOT NULL,
	REPLY_CONTENT	VARCHAR2(2000) NOT NULL,	
    BOARD_REF NUMBER,
	REPLY_REPLY_REF	NUMBER,
	REPLY_LEV	NUMBER DEFAULT 1,
	REPLY_SEQ NUMBER DEFAULT 1,
	REPLY_READCOUNT	NUMBER DEFAULT 0,
	REPLY_DATE	DATE DEFAULT SYSDATE,
  CONSTRAINT PK_REPLY PRIMARY KEY (REPLY_NUM),
  CONSTRAINT FK_REPLY_WRITER FOREIGN KEY (REPLY_WRITER) REFERENCES MEMBER (USERID) ON DELETE CASCADE,
  CONSTRAINT FK_BOARD_REF FOREIGN KEY (BOARD_REF) REFERENCES BOARD (BOARD_NUM) ON DELETE CASCADE,
  CONSTRAINT FK_RREPLY_REF FOREIGN KEY (REPLY_REPLY_REF) REFERENCES REPLY (REPLY_NUM) ON DELETE CASCADE
);

COMMENT ON COLUMN REPLY.REPLY_NUM IS '��۹�ȣ';
COMMENT ON COLUMN REPLY.REPLY_WRITER IS '����ۼ��ھ��̵�';
COMMENT ON COLUMN REPLY.REPLY_TITLE IS '�������';
COMMENT ON COLUMN REPLY.REPLY_CONTENT IS '��۳���';
COMMENT ON COLUMN REPLY.REPLY_DATE IS '����ۼ���¥';
COMMENT ON COLUMN REPLY.BOARD_REF IS '���۹�ȣ';  -- ���۹�ȣ 
COMMENT ON COLUMN REPLY.REPLY_REPLY_REF IS '������۹�ȣ';  -- ������ ��/��� : �ڱ��ȣ, ����� ��� : ������/��۹�ȣ
COMMENT ON COLUMN REPLY.REPLY_LEV IS '��۴ܰ�'; -- ������ ��� : 1, ����� ��� : 2
COMMENT ON COLUMN REPLY.REPLY_SEQ IS '��ۼ���'; -- ���� ������ ����� �� : 1, 2, 3, ....... ����ó�� (�ֽű��� 1)
COMMENT ON COLUMN REPLY.REPLY_READCOUNT IS '������ȸ��';

INSERT INTO BOARD 
VALUES (1, 'admin', '������ �Խñ�', '���� ����Ʈ�� �̿��� �ּż� �����մϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (2, 'user01', 'MVC Model2', '�� ���ø����̼� ���� ��� �� MVC ������ ���� ��2 ����� �� �����Դϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (3, 'user02', '������2', '������ 2��°�δ� First Controller �� ����ϴ� ����� �ֽ��ϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (4, 'user01', '������3', '3��° ����� �׼��� �̿��ϴ� ����Դϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (5, 'user01', 'MVC Model2', '�� ���ø����̼� ���� ��� �� MVC ������ ���� ��2 ����� �� �����Դϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (6, 'user02', '������2', '������ 2��°�δ� First Controller �� ����ϴ� ����� �ֽ��ϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (7, 'user01', '������3', '3��° ����� �׼��� �̿��ϴ� ����Դϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (8, 'user01', 'MVC Model2', '�� ���ø����̼� ���� ��� �� MVC ������ ���� ��2 ����� �� �����Դϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (9, 'user02', '������2', '������ 2��°�δ� First Controller �� ����ϴ� ����� �ֽ��ϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (10, 'user01', '������3', '3��° ����� �׼��� �̿��ϴ� ����Դϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (11, 'user01', 'MVC Model2', '�� ���ø����̼� ���� ��� �� MVC ������ ���� ��2 ����� �� �����Դϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (12, 'user02', '������2', '������ 2��°�δ� First Controller �� ����ϴ� ����� �ֽ��ϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

INSERT INTO BOARD 
VALUES (13, 'user01', '������3', '3��° ����� �׼��� �̿��ϴ� ����Դϴ�.', 
NULL, NULL, DEFAULT, DEFAULT);

SELECT * FROM BOARD;

COMMIT;

-- board ���̺� ��ȸ�� ���� ó��
update board
set board_readcount = 123
where board_num = 7;

update board
set board_readcount = 77
where board_num = 3;

update board
set board_readcount = 12
where board_num = 10;

COMMIT;


-- REPLY ���̺� ������ INSERT
INSERT INTO REPLY 
VALUES (1, 'user01', '������ �Խñ� ���', 
'���� ����Ʈ�� �̿��� �ּż� �����մϴ�. ����Դϴ�. ����Դϴ�.', 
1, 1, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (2, 'user02', 'MVC Model2 ���', 
'�� ���ø����̼� ���� ��� �� MVC ������ ���� ��2 ����� �� �����Դϴ�. ����Դϴ�.', 
2, 2, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (3, 'user01', '������2 ���', 
'������ 2��°�δ� First Controller �� ����ϴ� ����� �ֽ��ϴ�. ����Դϴ�.', 
3, 3, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (4, 'user02', '������3 ���', 
'3��° ����� �׼��� �̿��ϴ� ����Դϴ�. ����Դϴ�.', 
4, 4, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (5, 'user02', 'MVC Model2 ���', 
'�� ���ø����̼� ���� ��� �� MVC ������ ���� ��2 ����� �� �����Դϴ�. ����Դϴ�.', 
5, 5, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (6, 'admin', '������2 ���', 
'������ 2��°�δ� First Controller �� ����ϴ� ����� �ֽ��ϴ�. ����Դϴ�.', 
6, 6, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (7, 'user02', '������3 ���', 
'3��° ����� �׼��� �̿��ϴ� ����Դϴ�. ����Դϴ�.', 
7, 7, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (8, 'user02', 'MVC Model2 ���', 
'�� ���ø����̼� ���� ��� �� MVC ������ ���� ��2 ����� �� �����Դϴ�. ����Դϴ�.', 
8, 8, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (9, 'user01', '������2 ���', 
'������ 2��°�δ� First Controller �� ����ϴ� ����� �ֽ��ϴ�. ����Դϴ�.', 
9, 9, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (10, 'user02', '������3 ���', 
'3��° ����� �׼��� �̿��ϴ� ����Դϴ�. ����Դϴ�.', 
10, 10, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (11, 'user02', 'MVC Model2 ���', 
'�� ���ø����̼� ���� ��� �� MVC ������ ���� ��2 ����� �� �����Դϴ�. ����Դϴ�.', 
11, 11, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (12, 'user01', '������2 ���', 
'������ 2��°�δ� First Controller �� ����ϴ� ����� �ֽ��ϴ�. ����Դϴ�.', 
12, 12, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

INSERT INTO REPLY 
VALUES (13, 'admin', '������3 ���', 
'3��° ����� �׼��� �̿��ϴ� ����Դϴ�. ����Դϴ�.', 
13, 13, DEFAULT, DEFAULT, DEFAULT, DEFAULT);

commit;

SELECT * FROM REPLY;

