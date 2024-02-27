<template>
    <paperDetailed ref="paperDetailedRef"></paperDetailed>
</template>

<script>
    import paperDetailed from "@/components/paperDetailed";

    export default {
        components: {
            paperDetailed,
        },
        mounted() {
            this.$refs.paperDetailedRef.setInputNumberControls(false);
        }
    };
</script>