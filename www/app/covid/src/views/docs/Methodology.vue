<template>
    <div>
        <h1>Rationale and Methodology</h1>

        <p>The U.S. Federal guidelines (available at <a target="_blank" href="https://www.whitehouse.gov/openingamerica/">https://www.whitehouse.gov/openingamerica</a>) 
        establish a set of "gating" criteria for determining when a region/state should be considered ready for graduated re-opening.</p>
        
        <p>This site attempts to analyze those criteria in a reasonable manner, using publicly-available data, to determine if those gating criteria have been met,
            and a region is ready for graduated re-opening.</p>
        

        <p>Specifically, we are looking at these criteria:
        <ul>
            <li>Downward trend in confirmed cases over a 14 day period</li>
            <li>A downward trend in percentage of positive test results over a 14 day period</li>
        </ul>
        To these two criteria we add a third, a downward trend in Covid19-attributed deaths over a 14 day period.  Based on the scores 
        in these three areas, we determine if a region is:
        <ul>
            <li>OPEN: trends are positive: region ready for graduated re-opening</li>
            <li>CLOSED: trends are overall negative: region should close or remain closed, as defined by the locality</li>
            <li>STATIC: trends are not changing from baseline: should remain as-is, either open or closed</li>
            <li>UNKNOWN: not enough data available to determine trends</li>
        </ul>
        <p class="important">Please Note: 
            <ul>
                <li>The U.S. Federal government has no actual jurisdiction to open or close any U.S. State.  These decisions
            are the responsibility of the Governors and officials of each state.</li>
                <li>This site does NOT look at all of the criteria established in the federal guidelines - only a subset. See the discussion of criteria below.</li>
                <li>The decision to open or close a state or county can reasonably be based on the criteria analyzed here: but 
                    there may be other factors, such as the status of the health-care system in a region, that override this analysis</li>
            </ul>
        </p> 
        <h2>Criteria</h2>
        <p>A closer look at the criteria we are using.</p>
        <h3>Confirmed Cases</h3>
        <p>We are using the confirmed cases numbers from the <a href="#data-johns-hopkins">John Hopkins data</a>.</p>
         <p>   -- Confirmed cases include presumptive positive cases and probable cases, in accordance with CDC guidelines as of April 14 --</p>
         <p>   This is a dataset in wide use throughout the world for Covid19 analysis. As such, it represents some of the best data available;
            but it is far from perfect.  There are presumed to be large numbers of individuals who either have or have had the SARS_CoV-2 virus
            who have not been confirmed. However, for purposes of establishing a trend, the reported confirmed cases give us a measure of what we
            can assume is some relatively stable percentage of the infected population.  Arguing against this assumption are two factors: 1)
            as time goes on our testing abilities get better, and so our numbers of confirmed cases will neccessarily go up and will distort
            the picture of the growth of new cases, and 2) there is an increased focus on testing as one of the gating criteria for re-opening 
            which will further distort the total numbers of confirmed cases in an upwards direction. Even with these problems, however, it is 
            obviously an important measure of the spread of the virus.</p>

        <h3>% Positive Tests</h3>
        <p>Positive and negative test result numbers are from the <a href="#data-covid-tracking-project">Covid Tracking Project</a></p>
        <p>As the number of tests administered, and the overall number of confirmed cases goes up, it is important to keep
        track of the proportion of positive test to negative tests.  This number, the percentage of positive test results, has a built in break
        on the inflationary tendency of increased testing to produce more confirmed cases.  In fact, where we have reliable test data, this is probably
        our best measure of the growth or decline of the virus in our communities. Unfortunately, we do not have available good test result numbers, 
        positive and negative, beyond the U.S. State level. <i>The lack of access to reliable numbers at the county level seriously impedes our analysis</i>.</p>

        <h3>Covid19-Related Deaths</h3>
        <p>We are using the death totals numbers from the <a href="#data-johns-hopkins">John Hopkins data</a></p>
        <p>-- Death totals in the US include confirmed and probable, in accordance with CDC guidelines as of April 14. --</p>
        <p>This criteria, which is widely and (presumably) reliably reported, is missing from the official federal guidelines. 
           Because Covid19-related deaths are presumably better reported than confirmed cases, this lagging indicator should give
            us a better picture of the overall spread/contraction of the virus than the confirmed cases numbers;
            so we are including it as one of our key criteria. There are a few factors that
        would argue against using covid19-related death counts as a criteria, but we don't believe they are disqualifying. 
        Nevertheless, they should be acknowledged:</p>
        <ol>

            <li>) Death counts may be exaggerated in an effort to make the Covid19 pandemic appear to be worse than it is, for political or other purposes.
            We have no reliable knowledge of this being the case.  It is certainly true that the methods currently used for counting
            Covid19-related deaths differ from the standard methods used to count or estimate flu-related deaths for other virus strains. That is 
            a discussion and an argument that will likely take many months and even years to fully work itself out.  But for purposes of use
            as a criteria, all we need is that the death count be reported consistently - regardless of whether the same criteria would be
            used to classify a death as flu-related for another virus.</li>

            <li>) Death counts may be grossly underreported because of reporting issues, or political pressure to keep counts low. There 
            has been much analysis and reporting on the phenomenon of the growth of unaccounted deaths during the Covid19 pandemic. These 
            unaccounted deaths, which are higher, in some places significantly higher, than the unaccounted deaths from previous years 
            would presumably be attributable, at least in part, to Covid19.  These analyses will again take months and perhaps years to 
            work out, and will be an important part of establishing the true mortality rate for the SARS_CoV-2 virus. However, for the 
            purposes of establishing a trend, all that is requiref is that the method of counting Covid19-related deaths remain consistent across
            our baseline and comparison periods.  While these methods on the ground may have changed, and even changed drastically, 
            over the course of the pandemic, it is reasonable to assume that the counting methods have remained stable for the 
            past 4 weeks.</li>

            <li>) Finally, the count of Covid19-related deaths is a lagging indicator. Since it takes approximately two weeks between the onset
            of Covid19 disease symptoms and death from the disease, on average, the count of deaths gives us a sense of the 
            containment or expansion of the virus two weeks prior to the date on which a death is counted. If the death count is going down,
            then we know that the spread of the virus was probably contracting two weeks prior to our count. So, it is a "lagging indicator."
            If the virus spread is being contained, then having a two-week lag between containment and measurement of that containment is 
            not a real problem. And because deaths are presumably better reported than confirmed cases, this lagging indicator would give
            us a better picture of the overall spread/contraction of the virus than the confirmed cases numbers.
            However, if the virus is not contained and is in fact spreading in the community, such as might be the case in a so-called 
            "second-wave" spread of the virus, then the lagging nature of the indicator becomes a problem.  The death count could be going 
            down for two weeks while a "second-wave" infection was actually increasing the numbers of infected individuals. For this reason 
            the death count can only be used in conjunction with the confirmed cases count and the % positive tests numbers. If the death 
            count disagrees with these other criteria, it must be regarded as suspect.</li>
        </ol>

      <h3>Other Criterea</h3>
        <p>Other criterea are listed in the U.S. Federal guidelines that are not used here. These include:
            <ul>
                <li>Downward trajectory of influenza-like illnesses reported within a 14-day period</li>
                <li>Downward trajectory of covid-like syndromic cases reported within a 14-day period</li>
                <li>Hospitals able to treat all patients without crisis care</li>
                <li>'Robust' testing programs in place for at-risk healthcare workers, including emerging antibody testing</li>
            </ul>

            The first three of these are healthcare-related numbers or conditions that have proven difficult to measure reliably. 
            
            For example, this statement from the <a href="https://covidtracking.com/data">Covid Tracking Project website</a>:

            <blockquote>
                States are currently reporting two fundamentally unlike statistics: 
                    current hospital/ICU admissions and cumulative hospitalizations/ICU admissions. 
                    Across the country, this reporting is also sparse. In short: it is impossible to assemble 
                    anything resembling the real statistics for hospitalizations, ICU admissions, or ventilator usage across the United States. 
                    As a result, we will no longer provide national-level summary hospitalizations, ICU admissions, or ventilator usage statistics on our site.

                <p>We will continue tracking these numbers on a state-by-state basis, but it does not feel responsible to continue to display 
                    these US-wide statistics for a general audience. These heavily caveated numbers will remain in the API for advanced data users</p>
            </blockquote>

            <p>Without reliable numbers publicly available we are not able to include these in our analysis.</p>
            
            <p>The fourth criteria, 'Robust' testing, is perhaps more measurable - we could define it as minimum number of tests administered per 1M in population for example.
                However, exactly what that standard would be is currently the subject of debate. We are not currently including it as a criteria but may revisit this
                if an agreed definition arises as to what constitutes an acceptable level of testing.</p>
        
        <h2>Data Sources</h2> 
        <p>We are using two main data sources:</p>
        <h3>Center for Systems Science and Engineering (CSSE) at Johns Hopkins University</h3>
          <a name="data-johns-hopkins"/>
            <p><a href="https://github.com/CSSEGISandData/COVID-19">https://github.com/CSSEGISandData/COVID-19</a></p>

        <h3>The Covid Tracking Project</h3>
                <a name="data-covid-tracking-project"/>
            <p><a href="https://covidtracking.com/">https://covidtracking.com/</a></p>
  


    </div>
</template>
<script>
export default {
    data(){
        return{}
    }    
}
</script>
<style scoped>
blockquote {
    padding:30px;
    margin: 0px 30px;
    background-color:#C0C0C0;
    color:white;
}
</style>