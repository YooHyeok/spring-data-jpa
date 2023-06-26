package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 300 ; i++) {
            memberRepository.save(new Member("user"+i, i));
        }
    }

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * 도메인 클래스 컨버터 적용 <br/>
     *
     * @param member
     * @return
     * @PathVariable은 그대로 두고 변수를 도메인 타입으로 지정해준다. <br/>
     * 들어온 id를 기준으로 파라미터 Member 타입을 확인하고 <br/>
     * 자동으로 쿼리를 날려준 뒤 객체에 반환한다. <br/>
     * 단순 쿼리 조회용으로만 사용해야 한다. <br/>
     * (트랜잭션이 없는 범위에서 엔터티를 조회했으므로, 엔터티를 변경해도 DB에 반영되지 않는다.)
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * [페이징]
     * 파라미터로 Pageable을 받을 수 있다. <br/>
     * url :  /members?page=0&size=3&sort=id,desc&sort=username,desc <br/>
     * 첫번째page에 3개씩 출력하고 id와 username을 기준으로 내림차순 정렬 <br/>
     * @param pageable <br/>
     * 실제로는 PageRequest 객체를 생성한다. <br/>
     * 바인딩 될 때 pageable이 있으면 PageRequest 객체를 생성하고 값을 채운뒤 <br/>
     * pageable에 Injection(주입) 해준다 <br/>
     * @return
     */
    @GetMapping("/members")
    public Page<Member> list(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    /**
     * 페이지 사이즈 글로벌 설정 테스트
     * url 1 :  /members2<br/>
     * url 2 :  /members2?page=0&size=301&sort=id,desc&sort=username,desc <br/>
     * max page size를 300으로 지정했기 때문에 301을 파라미터로 지정하더라도 300개만 출력한다.
     * @param pageable
     * @return
     */
    @GetMapping("/members2")
    public Page<Member> list2(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    /**
     * 페이지 사이즈 개별 설정 테스트 <br/>
     * url 1 :  /members3<br/>
     * url 2 :  /members3?page=0&size=301&sort=id,desc&sort=username,desc <br/>
     * max page size를 300으로 지정했기 때문에 301을 파라미터로 지정하더라도 300개만 출력한다. <br/>
     * 개별 설정으로 13을 지정했더라도 url주소로 다른 값을 받아들일 수 있다. <br/>
     * (파라미터로 301을 지정하면 limit이 302가 된다.)
     * @param pageable
     * @return
     */
    @GetMapping("/members3")
    public Page<Member> list3(
            @PageableDefault(size = 13, sort = "username",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    /**
     * 페이징 처리 - 접두사 <br/>
     * 페이지 정보가 둘 이상이면 접두사로 구분한다.
     * @Qualifier에 접두사명을 추가한다("접두사명")
     * @url : /members4?member_page=0&order_page=1
     * @param memberPageable
     * @param orderPageable
     */
    @GetMapping("/members4")
    public String list4(@Qualifier("member") Pageable memberPageable,
                       @Qualifier("order") Pageable orderPageable) {
        return "";
    }

    /**
     * 페이지 내용 DTO로 변환
     * @param pageable
     * @return
     */
    @GetMapping("/members5")
    public Page<MemberDto> list5(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        //DTO로 변환
//        Page<MemberDto> pageDto = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//        Page<MemberDto> pageDto = page.map(member -> new MemberDto(member)); //DTO에 선언한 객체타입 생성자를 사용한다.
        Page<MemberDto> pageDto = page.map(MemberDto::new); //위 코드를 메서드 레퍼런스로 변환한다.
        return pageDto;
    }
}
